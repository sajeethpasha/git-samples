# Work Order Management Functional Flow Steps

## 1. Viewing Reservations by Work Order ID

**Description:**

- The system retrieves reservations linked to a specific work order using the provided work order ID.
- If the work order ID is invalid, an error is returned.
- Details include reserved materials, quantities, and relevant inventory locations.
- The system logs the retrieved reservations for future tracking.

**Code Reference:**
- `getReservationsByWorkOrder(Long id)`

---

## 2. Fetching Reservation Details

**Description:**

- The system fetches detailed reservations for a specified work order ID.
- If the work order is not found, an error message is generated.
- Reservation details include item numbers, reserved quantities, and supply locations.
- Logs capture the reservation retrieval process, including failed lookups.

**Code Reference:**
- `getReservationWithDetails(Long workOrderId)`

---

## 3. Fetching On-Hand Inventory Details

**Description:**

- On-hand inventory details are retrieved for materials linked to the work order.
- Inventory checks include reserved quantities, available quantities, and unreserved stocks.
- Supply details like subinventory codes, locators, and lot numbers are verified.
- Logs provide insights into API requests and responses during this process.

**Code Reference:**
- `fetchOnHandDetailsSoapAPI(WorkOrder workOrder)`

---

## 4. Checking Material Reservations

**Description:**

- Material reservations are verified against live inventory data.
- The system updates material records if reservations are confirmed.
- Missing or incomplete reservations trigger alerts for corrective action.
- Detailed logs track verification attempts and corresponding outcomes.

**Code Reference:**
- `checkReservationForMaterial(WorkOrder workOrder, WorkOrderMaterial material)`

---

## 5. Mapping Inventory Reservation Details

**Description:**

- The system maps inventory reservation data into work order details.
- Relevant fields include reservation IDs, item numbers, and reserved quantities.
- Mapped details are stored and made available for future processes.
- Logs maintain mapping operations for reference and troubleshooting.

**Code Reference:**
- `getWorkOrderReservation(InventoryReservation inventoryReservation)`

---

## 6. Fetching On-Hand Details for Reservations

**Description:**

- On-hand inventory details for reservations are retrieved from the inventory management system.
- Subinventory codes, item numbers, lot numbers, and locator names are queried.
- Accurate on-hand details ensure correct reservation assignments.
- Logs document inventory queries and responses for debugging.

**Code Reference:**
- `getInventoryOnhandDetailsForReservation(String subinventoryCode, String itemNumber, String lotNumber, String locatorName)`

---

## Notes

- **Reactive Programming:** Methods use `Mono` and `Flux` for non-blocking operations.
- **Logging:** Detailed logs support auditing and troubleshooting efforts.
- **Error Handling:** Custom exceptions ensure graceful failures.
- **Security:** Azure tokens are retrieved dynamically for secured API requests.

