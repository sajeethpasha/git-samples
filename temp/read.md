# Manual Inventory Reservation Process

This document outlines the process for handling manual inventory reservations within the system, detailing the flow from the controller endpoint to the final response.

## 1. Entry Point: Controller Endpoint

- **Endpoint:** `POST /manual-reservation`
- **Location:** Inventory Domain Controller
- **Method:** `postManualInventoryReservation`
- **Request Body:** An instance of `InventoryManualReservationRequest`
- **Response:** A reactive `Mono` wrapped in a `ResponseEntity` with HTTP status `201 CREATED`

**Process:**

1. The controller receives the manual reservation request.
2. It calls the service layer method `createManualInventoryReservation(body)` from the item service.
3. The controller logs the final response before returning it to the caller.

## 2. Service Layer: Creating the Manual Reservation

- **Method:** `createManualInventoryReservation`
- **Location:** Item Service
- **Input:** The complete request (`InventoryManualReservationRequest`)

**Process:**

1. **Extract Reservation Details:**
   - Retrieves `InventoryManualReservationCreateRequest` from the request body.
2. **Determine Processing Path:**
   - Checks the flag `transformWorkOrders` within the create request.
   - If `TRUE`:
     - Calls a method named `processAndValidateTransformWorkOrdersForHardReservation` to handle work order transformation reservations.
   - If `FALSE`:
     - Calls `processAndValidateProcessWorkOrdersForHardReservation` to handle the standard process.

## 3. Validation and Processing Work Orders

- **Method:** `processAndValidateProcessWorkOrdersForHardReservation`
- **Location:** Item Service
- **Input:** The original request (`InventoryManualReservationRequest`) and the extracted create request

**Process:**

1. **Retrieve Work Order Materials:**
   - Calls `workOrderDomainClient.listWorkOrderMaterials` using the `demandSourceHeaderId` from the create request.
   - This call reaches out to the work order domain (via HTTP GET) to retrieve a list of materials.
2. **Filter for Matching Material:**
   - The returned list is filtered for a `WorkOrderMaterial` that matches:
     - `workOrderOperationId` equals `demandSourceLineId`
     - `itemNumber` equals the item number from the create request.
3. **Validate Reservation Quantity:**
   - Logs the attempt to validate the reservation quantity.
   - If a matching work order material exists, compares:
     - Reservation Quantity vs. Available-to-Reserve Quantity
   - **Error Condition:**
     - If the reservation quantity exceeds the available quantity, the flow terminates with an `InventoryManualReservationCreationException`.
4. **Proceed to Hard Reservation Processing:**
   - If validation passes, the method calls `processHardReservation` to either update an existing reservation or create a new one.

## 4. Processing the Hard Reservation

- **Method:** `processHardReservation`
- **Location:** Item Service
- **Input:** The original request and the create request

**Decision Point:**

- **Existing Reservation (Update):**
  - **Condition:** The create request contains a non-empty `reservationId` (indicating a soft reservation exists).
  - **Action:**
    - Logs the details (item number, sub-inventory code, locator ID, lot number) for updating.
    - Invokes `updateManualReservation` to convert the soft reservation to a hard reservation.
    - Error handling is added via a `doOnError` callback to log any issues during update.
- **New Reservation (Create):**
  - **Condition:** No existing `reservationId` is provided.
  - **Action:**
    - Logs that a new reservation is being created.
    - Calls `createAndUpdateManualReservation` to create and then update the reservation.
    - Also uses a `doOnError` callback for error logging.

## 5. Creating and Updating a New Reservation

- **Method:** `createAndUpdateManualReservation`
- **Location:** Item Service

**Process:**

1. **Create the Manual Reservation:**
   - Calls `client.createManualInventoryReservation(manualReservationCreateRequest)` on the inventory domain client.
   - This sends a `POST` request to the `/manual-reservation` endpoint of the inventory domain.
   - The client method logs a CURL command for debugging and returns a `Mono` of `InventoryManualReservationResponse` which includes the generated `reservationId`.
2. **Update the Reservation to Hard Reservation:**
   - Using the returned `reservationId`, it builds an `InventoryReservationUpdateRequest` (patch body) with key details:
     - `lotNumber`
     - `subinventoryCode`
     - `locatorId`
   - Calls `updateReservation(reservationId, patchBody)` to finalize the hard reservation.

## 6. Updating the Reservation (PATCH Operation)

- **Method:** `updateReservation`
- **Location:** Inventory Domain Client

**Process:**

1. **Delegation to the Inventory Client:**
   - Calls `updateInventoryReservation(reservationId, patchBody)` defined in the inventory client.
2. **Inside `updateInventoryReservation`:**
   - **HTTP PATCH Request:**
     - Constructs the URL `/reservation/{reservationId}`.
     - Sets the authorization header using the `AzureToken` from the context.
     - Sets the request body with the update details.
     - Accepts JSON responses.
   - **Error Handling:**
     - Uses `.onStatus` to intercept HTTP 4xx/5xx responses.
     - Transforms error responses into an `InventoryDomainException`.
   - **Mapping the Response:**
     - On a successful response, maps the returned JSON into an `InventoryReservation` object.
     - Logs the response.
   - **Wrapping the Result:**
     - Once the PATCH call succeeds, the response is wrapped into an `InventoryManualReservationResponse` and returned.

## 7. Work Order Domain: Listing Work Order Materials

- **Method:** `listWorkOrderMaterials`
- **Location:** Work Order Domain Controller

**Process:**

1. Constructs a `GET` request to the endpoint:
   - `http://localhost:8081/work-orders/{workOrderId}/materials?limit=500`
2. Sets the authorization and accept headers.
3. Logs a CURL command for reference.
4. Retrieves a paged response and maps it to a list of `WorkOrderMaterial` objects.
5. This data is used in the validation step (see Section 3).

## 8. Final Outcome and Response

- **Successful Flow:**
  - If all validations pass and the hard reservation is either successfully updated or created, the final `InventoryManualReservationResponse` is sent back to the client with a `201` status.
- **Error Flow:**
  - If any error occurs (e.g., validation failure or HTTP error during PATCH/POST), the respective exception (such as `InventoryManualReservationCreationException`, `InventoryDomainException`, or `WorkOrderDomainException`) is thrown and logged.

## Summary Diagram of the Flow

```mermaid
graph TD
    A[POST /manual-reservation (Controller)] -->|Calls| B[ItemService.createManualInventoryReservation]
    B -->|Extracts request and checks transformWorkOrders flag| C{transformWorkOrders flag}
    C -->|False| D[processAndValidateProcessWorkOrdersForHardReservation]
    