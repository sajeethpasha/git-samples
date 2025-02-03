# Manual Inventory Reservation Process

This document provides a detailed overview of the manual inventory reservation process, covering the flow from the controller endpoint to the final response.

---

## 1. Entry Point: Controller Endpoint

- **Method:** `postManualInventoryReservation` (Inventory Domain Controller)
- **Endpoint:** `POST /manual-reservation`

### **What It Does:**
1. Accepts a request body of type `InventoryManualReservationRequest`.
2. Calls the service layer method `createManualInventoryReservation(body)`.
3. Wraps the returned reactive stream (`Mono<InventoryManualReservationResponse>`) in an HTTP `201 Created` response.
4. Logs the final response.

---

## 2. Service Layer: `createManualInventoryReservation`

- **Method:** `createManualInventoryReservation(InventoryManualReservationRequest body)`

### **Key Steps:**
1. Extracts the nested `InventoryManualReservationCreateRequest` from the incoming request.
2. **Branching Logic:**
   - If `transformWorkOrders` is `true`, it calls `processAndValidateTransformWorkOrdersForHardReservation` to handle transforming work orders.
   - Otherwise, it calls `processAndValidateProcessWorkOrdersForHardReservation` to process the work orders for a hard reservation.

---

## 3. Validating Work Order Materials

- **Method:** `processAndValidateProcessWorkOrdersForHardReservation(InventoryManualReservationRequest body, InventoryManualReservationCreateRequest manualReservationCreateRequest)`

### **Key Steps:**
1. Calls the **Work Order Domain Client’s** method `listWorkOrderMaterials` with `demandSourceHeaderId` from the create request.
2. **Inside `listWorkOrderMaterials`:**
   - Issues a `GET` request to `/work-orders/{workOrderId}/materials?limit=500`.
   - Includes an `Authorization` header (AzureToken from Reactor context).
   - Transforms the response into a list of `WorkOrderMaterial` objects.
   - Logs a `curl` command for debugging.
3. **Back in the Service Method:**
   - Filters the list of work order materials to find one that matches:
     - `workOrderOperationId` equals `demandSourceLineId`
     - `itemNumber` equals the provided item number.
   - Validates that the `reservationQuantity` does not exceed the `availableToReserve` amount.
   - If validation fails, raises an `InventoryManualReservationCreationException`.
   - If validations pass, calls `processHardReservation` to create or update the reservation.

---

## 4. Processing the Hard Reservation

- **Method:** `processHardReservation(InventoryManualReservationRequest body, InventoryManualReservationCreateRequest manualReservationCreateRequest)`

### **Key Steps:**
1. **Update vs. Create Decision:**
   - **Update Scenario:** If `reservationId` exists:
     - Logs that it will update the existing (soft) reservation to a hard reservation.
     - Calls `updateManualReservation(manualReservationCreateRequest)`.
   - **Create Scenario:** If `reservationId` is not provided:
     - Logs that a new reservation is being created.
     - Calls `createAndUpdateManualReservation(manualReservationCreateRequest)`.

---

## 5. Creating and Updating the Reservation

- **Method:** `createAndUpdateManualReservation(InventoryManualReservationCreateRequest manualReservationCreateRequest)`

### **Key Steps:**
1. **Create Manual Reservation:**
   - Calls `Inventory Domain Client.createManualInventoryReservation(manualReservationCreateRequest)`.
2. **Inside the Client Method:**
   - Issues a `POST` request to `/manual-reservation` on the inventory domain service.
   - Includes:
     - `Authorization` header (token from Reactor context).
     - `Request body` (inventoryManualReservationCreateRequest).
   - Logs the `curl` command for traceability.
   - Handles errors (`HTTP 4xx` or `5xx`) by mapping them to `InventoryDomainException`.
   - Converts the response to an `InventoryManualReservationResponse` containing the `reservationId`.
   - Logs the received response.
3. **Update Reservation Details:**
   - Builds an `InventoryReservationUpdateRequest` (`patchBody`) with:
     - `lotNumber`
     - `subinventoryCode`
     - `locatorId`
   - Calls `updateReservation(reservationId, patchBody)` to convert the soft reservation to a hard reservation.

---

## 6. Updating the Reservation

- **Method:** `updateReservation(Long reservationId, InventoryReservationUpdateRequest patchBody)`

### **Key Steps:**
1. Invokes `Inventory Client Domain.updateInventoryReservation(reservationId, patchBody)`.
2. **Inside `updateInventoryReservation`:**
   - **HTTP Request:**
     - Uses a `PATCH` request to `/reservation/{reservationId}`.
     - Sets the request body with `InventoryReservationUpdateRequest`.
     - Includes `Authorization` header (AzureToken from Reactor context).
     - Accepts JSON responses.
   - **Error Handling:**
     - Handles `4xx/5xx` errors by extracting them into `ApiError` and wrapping them in `InventoryDomainException`.
   - **Logging:**
     - Logs the response received after updating.
   - **Response Handling:**
     - Converts the response to an `InventoryReservation` object.
     - Wraps the response in an `InventoryManualReservationResponse` and returns.

---

## 7. Final Response

- The `InventoryManualReservationResponse` is passed back through the reactive chain.
- The **Inventory Domain Controller** wraps it in a `ResponseEntity` with HTTP status **201 (Created)**.
- The flow concludes with the client receiving the final response indicating a successful manual inventory reservation.

---

## 8. Summary Diagram

```mermaid
graph TD
    A[Client Request] --> B[Inventory Domain Controller]
    B --> C[Item Service: createManualInventoryReservation(body)]
    C --> D{Check transformWorkOrders flag}
    D -->|False| E[processAndValidateProcessWorkOrdersForHardReservation]
    E --> F[workOrderDomainClient.listWorkOrderMaterials]
    F --> G[Validate WorkOrderMaterial & Reservation Qty]
    G --> H[processHardReservation(body, manualReservationCreateRequest)]
    H -->|If reservationId exists| I[updateManualReservation]
    H -->|Else| J[createAndUpdateManualReservation]
    J --> K[Inventory Domain Client: createManualInventoryReservation (POST)]
    K --> L[Build patchBody (lotNumber, subinventoryCode, locatorId)]
    L --> M[Inventory Domain Client: updateReservation (PATCH)]
    M --> N[Returns InventoryReservation wrapped in InventoryManualReservationResponse]
    N --> O[Final Response (HTTP 201) returned to the client]
```

---

## 9. Additional Notes

### **Error Handling:**
- Each external call (work order retrieval and inventory updates) handles `HTTP` errors (`4xx/5xx`).
- Errors are mapped to custom exceptions (`WorkOrderDomainException` or `InventoryDomainException`).
- Logs and propagates errors correctly.

### **Logging & Debugging:**
- The system logs critical steps, including:
  - Constructed `CURL` commands for external calls.
  - Validation steps and error conditions.
  - Responses from external systems (both create and update operations).

### **Reactive Programming:**
- The entire flow is implemented using **Reactor’s `Mono`**, allowing non-blocking, asynchronous processing.

---

This documentation provides a comprehensive understanding of how manual inventory reservations are processed, covering all key interactions between the **controller, service layer, and external clients**. 🚀