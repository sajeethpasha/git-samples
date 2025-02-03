Below is a detailed document that explains the complete flow for the manual inventory reservation process—from the initial REST endpoint call through the service logic and client calls—along with the method-calling sequence and validation steps.

---

## 1. REST Endpoint – Controller Layer

### Method: postManualInventoryReservation  
- **Location:** *Inventory Domain Controller*  
- **Endpoint:** `POST /manual-reservation`  
- **Request Payload:** An instance of `InventoryManualReservationRequest`  
- **Response:** A reactive `Mono<InventoryManualReservationResponse>` wrapped in a `ResponseEntity` with HTTP status **201 CREATED**

**What Happens:**  
- The controller receives the request and logs the incoming data.  
- It delegates processing to the `itemService.createManualInventoryReservation(body)` method.  
- Once the service returns the result, it logs the final response and sends it back to the client.

---

## 2. Service Layer – Entry into Business Logic

### Method: createManualInventoryReservation  
- **Location:** *Item Service*  
- **Input:** The complete `InventoryManualReservationRequest`  
- **Processing:**  
  1. **Extract Manual Reservation Data:**  
     - Retrieves the inner `InventoryManualReservationCreateRequest` from the request.
  2. **Determine Reservation Flow Based on Flag:**  
     - Checks the Boolean flag `transformWorkOrders` within the create request.
     - **If `transformWorkOrders` is TRUE:**  
       - Calls `processAndValidateTransformWorkOrdersForHardReservation` (not detailed in the snippet but following similar logic tailored to transformed work orders).
     - **If `transformWorkOrders` is FALSE:**  
       - Calls `processAndValidateProcessWorkOrdersForHardReservation` to validate and process the reservation.

---

## 3. Validating Against Work Order Materials

### Method: processAndValidateProcessWorkOrdersForHardReservation  
- **Location:** *Item Service*  
- **Inputs:**  
  - The original `InventoryManualReservationRequest`  
  - The extracted `InventoryManualReservationCreateRequest`  
- **Steps:**  
  1. **Fetch Work Order Materials:**  
     - Invokes `workOrderDomainClient.listWorkOrderMaterials` using `manualReservationCreateRequest.getDemandSourceHeaderId()`.
     - This client call sends a GET request to the work order domain to fetch materials associated with a given work order.
  2. **Filter and Validate Material:**  
     - Filters the returned list to find a material matching:
       - `workOrderOperationId` equal to `demandSourceLineId`  
       - `itemNumber` equal to the requested item number  
     - Logs the reservation quantity validation step.
  3. **Quantity Check:**  
     - If a matching material is found, it compares the requested `reservationQuantity` with the provided `availableToReserve`.
     - **Error Handling:**  
       - If the reservation quantity exceeds the available quantity, a custom exception (`InventoryManualReservationCreationException`) is raised.
  4. **Proceeding to Hard Reservation Processing:**  
     - If validation passes, the flow continues by calling `processHardReservation`.

---

## 4. Processing the Hard Reservation

### Method: processHardReservation  
- **Location:** *Item Service*  
- **Inputs:** The full reservation request and the create request  
- **Decision Logic:**  
  - **Existing Reservation Update:**  
    - If the create request contains a non-empty `reservationId` (indicating an existing soft reservation), the method logs the update details (item number, sub-inventory code, locator id, lot number) and calls `updateManualReservation`.
    - A logging callback (`doOnError`) captures any errors during the update process.
  - **New Reservation Creation:**  
    - If there is no `reservationId`, it logs that a new reservation is being created and calls `createAndUpdateManualReservation`.
    - Likewise, errors during creation are logged.

*Note:* Although the code snippet shows only one branch in detail, a similar pattern would exist for the `processAndValidateTransformWorkOrdersForHardReservation` method when the flag is true.

---

## 5. Creating and Updating a New Reservation

### Method: createAndUpdateManualReservation  
- **Location:** *Item Service*  
- **Steps:**  
  1. **Create the Reservation:**  
     - Calls the inventory domain client’s method `createManualInventoryReservation`, passing the create request.
     - This client method issues an HTTP POST to `/manual-reservation` and logs the corresponding CURL command.
     - It returns a `Mono<InventoryManualReservationResponse>` that contains the new `reservationId`.
  2. **Update Reservation Details (Patch):**  
     - Once the new reservation is created, the service builds an `InventoryReservationUpdateRequest` (the patch body) with key details:  
       - `lotNumber`  
       - `subinventoryCode`  
       - `locatorId`
     - It then calls `updateReservation` with the newly obtained `reservationId` and patch body to convert the reservation into a hard reservation.

---

## 6. Client Calls – Communication with Inventory Domain

### a. Creating a Reservation

#### Method: createManualInventoryReservation (Inventory Domain Client)  
- **Location:** *Inventory Domain Client*  
- **Process:**  
  - Constructs a POST request to `/manual-reservation`.
  - Sets headers such as `Authorization` (using an Azure token from the reactive context) and `Content-Type`.
  - Logs a detailed CURL command for debugging purposes.
  - Handles error statuses (HTTP 4xx/5xx) by mapping them to an `InventoryDomainException`.
  - Parses the response into an `InventoryManualReservationResponse`.

### b. Updating the Reservation

#### Method: updateReservation (Inventory Domain Client)  
- **Steps:**  
  - Invokes `updateInventoryReservation` on the inventory client using the `reservationId` and patch body.
  - Wraps the updated `InventoryReservation` into an `InventoryManualReservationResponse`.
  
#### Method: updateInventoryReservation (Inventory Client – Domain)  
- **Location:** *Inventory Client: Domain*  
- **Process:**  
  - Sends an HTTP PATCH request to `/reservation/{id}`.
  - Constructs the URL using the provided `reservationId` and sets the request body with the update details.
  - Uses the Azure token for authentication and accepts JSON responses.
  - Implements error handling for 4xx/5xx responses, converting them into an `InventoryDomainException`.
  - Logs the response and returns a parsed `InventoryReservation`.

---

## 7. Work Order Domain – Supporting Validation

### Method: listWorkOrderMaterials  
- **Location:** *Work Order Domain Controller*  
- **Process:**  
  - Builds a GET request to the endpoint:  
    `http://localhost:8081/work-orders/{workOrderId}/materials?limit=500`
  - Sets required headers (Authorization, Accept) and logs a corresponding CURL command.
  - Processes the paginated response to extract a list of `WorkOrderMaterial` objects.
  - This data is then used in the validation step of `processAndValidateProcessWorkOrdersForHardReservation`.

---

## 8. Overall Flow Diagram

1. **Client Request:**
   - **POST /manual-reservation** is received by the controller.
2. **Controller:**
   - Calls `itemService.createManualInventoryReservation(body)`.
3. **Service Layer:**
   - **Extracts** the `InventoryManualReservationCreateRequest`.
   - **Branching Decision:**  
     - If `transformWorkOrders` is true, calls `processAndValidateTransformWorkOrdersForHardReservation`.  
     - Otherwise, calls `processAndValidateProcessWorkOrdersForHardReservation`.
4. **Validation:**
   - **processAndValidateProcessWorkOrdersForHardReservation:**  
     - Calls `workOrderDomainClient.listWorkOrderMaterials(...)` to fetch work order materials.
     - **Validates** that the requested reservation quantity does not exceed the available quantity.
5. **Hard Reservation Processing:**
   - **processHardReservation:**  
     - **If an existing reservation exists:**  
       - Calls `updateManualReservation` to update (convert soft to hard reservation).
     - **If no reservation exists:**  
       - Calls `createAndUpdateManualReservation` to create a new reservation.
6. **Creating a New Reservation:**
   - **createAndUpdateManualReservation:**  
     - Calls `client.createManualInventoryReservation(...)` (via inventory domain client) to create the reservation.
     - Uses the returned `reservationId` to build a patch body.
     - Calls `updateReservation` to update the reservation details.
7. **Updating the Reservation:**
   - **updateReservation:**  
     - Delegates to `client.updateInventoryReservation(...)` to perform the PATCH operation.
     - Wraps the result in an `InventoryManualReservationResponse`.
8. **Response:**
   - The final response is logged and returned to the controller, which then sends it back to the client with HTTP status 201.

---

## 9. Error Handling and Logging

- **At Each Stage:**  
  - The flow uses reactive error handling (via `.onStatus` and `Mono.error`) to capture HTTP errors and validation failures.
  - Custom exceptions such as `InventoryManualReservationCreationException` and `InventoryDomainException` are thrown where appropriate.
  - Logging is pervasive throughout the flow, including:
    - CURL command logging in client calls (for debugging).
    - Detailed logs on validation steps and reservation creation/update.
    - Logging of any errors encountered during each stage.

---

## Conclusion

This complete flow shows a layered approach:
- The controller acts as the entry point.
- The service layer determines which branch to follow based on the reservation’s transformation flag.
- Validation is performed using work order material data from an external service.
- Depending on whether a reservation already exists, the process either updates an existing reservation or creates a new one.
- The client calls to the inventory domain handle both creation (via POST) and update (via PATCH), with robust error handling and logging throughout the process.

This detailed explanation should provide management with a clear overview of the method call process and the complete flow of manual inventory reservation handling in the system.