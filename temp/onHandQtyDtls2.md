**API Documentation for Management Review**  
*Get Onhand Quantity Details*  

---

### **1. Overview**  
This API retrieves real-time onhand inventory details (e.g., available stock, reservable quantities) for a specific organization, with optional filters for subinventory, item, and location. It combines data from two sources:  
1. **BI Proxy Service**: Initial onhand data (basic quantities).  
2. **Reservation SOAP API**: Enriches data with transaction-specific metrics (e.g., `AvailableToTransact`, `AvailableToReserve`).  

---

### **2. API Endpoint Details**  
- **Endpoint**: `GET /inv/onHandQtyDtls`  
  - **Parameters**:  
    - `OrgCode` (Required): Organization code (e.g., `ORG_MAIN`).  
    - `Subinventory-code` (Optional): Filters results by storage subinventory.  
    - `item-number` (Optional): Filters results by item ID (e.g., `ITM-1001`).  
- **Response**:  
  - `200 OK`: Returns enriched onhand details (e.g., quantities, locator, lot numbers).  
  - `404 Not Found`: Returned if no matching inventory records exist.  

---

### **3. Key Features & Logic**  
#### **Data Flow**  
1. **Initial Data Fetch**:  
   - Retrieve base onhand data from the BI Proxy service using filters (`OrgCode`, `Subinventory`, `Item`).  
2. **Data Enrichment**:  
   - If initial data is **non-empty**, call a SOAP API to append reservation-specific metrics (e.g., `AvailableToTransact`).  
   - If initial data is **empty**, return an empty list immediately.  
3. **Locator Formatting**:  
   - Automatically replaces hyphens (`-`) with periods (`.`) in locator codes (e.g., `AISLE-1` → `AISLE.1`).  

#### **Filtering & Validation**  
- **Input Sanitization**: All parameters are sanitized to prevent injection attacks.  
- **Error Resilience**:  
  - SOAP API failures are gracefully ignored, preserving base data.  
  - Empty responses from external services return `404 Not Found`.  

---

### **4. Error Handling**  
- **External Service Failures**:  
  - BI Proxy or SOAP API errors are logged, and partial data is returned if possible.  
  - Critical errors (e.g., invalid tokens) trigger a `500 Internal Server Error`.  
- **Custom Exceptions**:  
  - `ListInventoryOnhandDetailsNotFoundException`: Raised if no data exists for the provided filters.  

---

### **5. Dependencies**  
1. **BI Proxy Service**:  
   - **Endpoint**: `GET /inv/onHandQtyDtls`  
   - **Purpose**: Provides base inventory data (e.g., locator, lot numbers).  
2. **Reservation SOAP API**:  
   - **Endpoint**: `GET /onhand-quantity/reservation`  
   - **Purpose**: Enriches data with reservable/transactable quantities.  
3. **Authentication**: Azure tokens secure all external service calls.  

---

### **6. Notes for Stakeholders**  
- **Performance**:  
  - Sequential processing of items may impact response times for large datasets.  
  - Recommend limiting use of optional filters (`item-number`, `Subinventory-code`) for optimal performance.  
- **Auditability**:  
  - Critical steps (e.g., data enrichment, errors) are logged for traceability.  
- **Business Impact**:  
  - Supports inventory reservation workflows by providing real-time stock visibility.  
  - Locator formatting ensures compatibility with downstream systems.  

---

**Approval & Next Steps**  
☐ Validate scalability with peak load simulations.  
☐ Confirm SOAP API SLAs for high-priority use cases.  
☐ Review security protocols for Azure token management.  

*Prepared by: [Your Name/Team]*  
*Date: [Insert Date]*