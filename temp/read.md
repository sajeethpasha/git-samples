# Work Order Reservation Flow Documentation

## Overview
This document explains the flow of work order reservation management, including fetching, updating, and managing inventory reservations. The flow is built using reactive programming with Project Reactor.

---

## 1. Listing Work Orders
### Method: `listWorkOrders(WorkOrderQuery query)`
- Logs the incoming query.
- Calls the decorated `listWorkOrders` method.

**API Call Details:**
- **HTTP Method:** GET
- **Endpoint:** `/work-orders`
- **Authorization:** Azure token-based.
- **Response:** List of `WorkOrder[]`

**Important Logs:**
- Request and response details for debugging.

---

## 2. View Reservations by Work Order ID
### Endpoint: `/{id}/reservations`
- **HTTP Method:** GET
- **Description:** Fetches reservations for a given work order ID.

**Swagger Annotations:**
- **Operation ID:** View-reservations-by-work-order-id
- **Responses:**
  - **200:** Reservations retrieved.
  - **404:** Reservations not found.

**Flow Steps:**
1. Calls `getReservationWithDetails(workOrderId)`.
2. Logs the final response.

---

## 3. Fetching On-Hand Details
### Method: `fetchOnHandDetailsSoapAPI(WorkOrder workOrder)`
- Fetches inventory details using a SOAP API.
- Updates each material with:
  - Quantity
  - Unreserved Quantity
  - Onhand Balances
  - Available to Reserve/Transact

**Error Handling:**
- Resumes with the current material in case of an error.

**Sorting:**
- Materials are sorted by sequence and operation sequence numbers.

---

## 4. Checking Reservations for a Material
### Method: `checkReservationForMaterial(WorkOrder workOrder, WorkOrderMaterial material)`
- Lists inventory reservations based on the material details.
- Updates the material with:
  - Supply Subinventory
  - Supply Locator
  - Lot Number
  - Reservation ID

**Important Logs:**
- Logs each reservation fetch and updates made.

---

## 5. Fetching Reservations with Details
### Method: `getReservationWithDetails(Long workOrderId)`
- Fetches a work order with reservations.
- Calls the following in sequence:
  1. `listWorkOrders` for the given ID.
  2. `fetchOnHandDetailsSoapAPI` for inventory updates.
  3. `checkReservationForMaterial` for each material.

**Error Handling:**
- Throws `MaterialTransactionException` if no work order is found.
- Logs key operations for debugging.

---

## Conclusion
This flow ensures that work orders are listed, checked, and updated efficiently. Each step is designed with reactive patterns to optimize performance and handle errors gracefully.

