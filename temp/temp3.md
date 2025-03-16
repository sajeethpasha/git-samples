**Documentation: View Inventory Reservations by Work Order ID**

---

### **Overview**
This document outlines the process and technical flow for retrieving inventory reservations associated with a specific work order. The feature allows users to view detailed reservation information, including materials, products, and inventory details tied to a work order.

---

### **Key Functionality**
1. **Endpoint**: `GET /{id}/reservations`  
   - Fetches inventory reservations for a work order by its ID.
   - Returns a structured response with reservation details or a `404` error if not found.

2. **Process Flow**:
   - Retrieve work order details (including materials/products).
   - Check inventory reservations for each material in the work order.
   - Enrich response with reservation-specific data (e.g., sub-inventory, locator, lot number).

---

### **Technical Process Flow**
1. **Retrieve Work Order**:
   - A query is sent to the **Work Order Service** to fetch the work order by ID.
   - Includes materials, products, and dynamic flex fields (DFFs).
   - If no work order is found, a `404` error is returned.

2. **Fetch Inventory Reservations**:
   - For each material in the work order:
     - **If reserved quantity is zero**:
       - Clear sub-inventory, locator, and lot number fields.
     - **If reserved quantity exists**:
       - Query the **Inventory Service** to fetch reservation details.
       - Map reservation data (sub-inventory, locator, lot, reservation ID) to the material.

3. **Response Enrichment**:
   - Reservation details are appended to the work order materials.
   - The final response includes the work order with enriched reservation data.

---

### **Key Components**
| Component | Description |
|-----------|-------------|
| **WorkOrderService** | Orchestrates fetching work orders and inventory reservations. |
| **InventoryService** | Provides inventory reservation data for materials. |
| **APIs Called** | <ul><li>Work Order Service (`/work-orders`)</li><li>Inventory Service (`/inventory-reservations`)</li></ul> |

---

### **Error Handling**
- **404 Not Found**: Returned if no work order exists for the provided ID.
- **5xx Errors**: Propagated from downstream services (e.g., Work Order Service, Inventory Service).
- **Custom Exceptions**:
  - `MaterialTransactionException`: Thrown if a work order has no reservations.
  - `WorkOrderDomainException`: Handles client errors (4xx) from external APIs.

---

### **Logging & Auditing**
- Logs are generated at critical steps:
  - Work order retrieval.
  - Inventory reservation checks.
  - Final API response (success/error).
- Example log entries:
  - `"Fetching workOrder Id {id} for reservations"`
  - `"Returning empty response as reservation is not available for item {itemNumber}"`

---

### **Dependencies**
1. **External Services**:
   - **Work Order Service**: Provides work order data (materials, products).
   - **Inventory Service**: Supplies inventory reservation details.
2. **Authentication**: Requires a valid `AzureToken` for external API calls.

---

### **Assumptions & Notes**
- **Pagination**: Inventory reservations are fetched with a default limit of 25 entries.
- **Reservation Matching**: Reservations are linked to materials via `itemNumber` and `demandSourceLineId`.
- **Performance**: 
  - Work orders are queried with a limit of 500 records to ensure completeness.
  - Parallel processing of materials is avoided to simplify error handling.

---

**Revision History**  
- v1.0 | [Date] | Initial Document.