**Documentation: Retrieve Valid Labels for Items**  

---

### **Overview**  
This feature enables users to fetch valid "labels" (items marked as labels) for a specific organization. Labels are enriched with unit of measure (UOM) codes for clarity. The endpoint ensures efficient data retrieval by leveraging caching and pagination.

---

### **Key Functionality**  
1. **Endpoint**: `GET /valid-labels/{organization-code}`  
   - Returns valid labels (items) for an organization, filtered by item number.  
   - Automatically enriches items with UOM codes.  
   - Throws an error if the item number is too short (`< 3 characters`).  

2. **Parameters**:  
   - `organization-code`: Organization identifier (path variable).  
   - `item-number`: Partial or full item number to filter results (query parameter, **required**).  
   - `limit`: Pagination limit (default: 500).  
   - `offset`: Pagination offset (optional).  

---

### **Technical Process Flow**  
1. **Validation**:  
   - Checks if the `item-number` query parameter has at least 3 characters.  
   - Throws `ItemNumberLengthException` if validation fails.  

2. **Fetch Labels**:  
   - Calls the **Item Service** to retrieve items flagged as labels, filtered by `item-number` and `organization-code`.  

3. **Enrich with UOM Codes**:  
   - Uses a **cached UOM map** to map `primaryUomValue` (e.g., "Each") to `uomCode` (e.g., "Ea").  
   - If the cache is empty, fetches UOM data from the **Unit of Measure Service** and populates the cache.  

4. **Response**:  
   - Returns a paginated list of items with enriched UOM codes.  

---

### **Key Components**  
| Component | Description |  
|-----------|-------------|  
| **ItemService** | Fetches items marked as labels and enriches them with UOM codes. |  
| **UnitsOfMeasureCache** | Caches UOM data to avoid redundant API calls. |  
| **APIs Called** | <ul><li>Item Service (`/items` with `onlyLabels=true`)</li><li>Unit of Measure Service (`/units-of-measure`)</li></ul> |  

---

### **Error Handling**  
- **404 Not Found**: Returned if no valid labels exist for the criteria.  
- **400 Bad Request**: Triggered if `item-number` has fewer than 3 characters (`ItemNumberLengthException`).  
- **4xx/5xx Errors**: Propagated from external services (e.g., Item Service, UOM Service).  

---

### **Logging & Auditing**  
- Logs the final response structure for auditing.  
- Example log entry: `"Final response of getValidLabels [Item(...)]"`.  

---

### **Dependencies**  
1. **External Services**:  
   - **Item Service**: Provides label-specific item data.  
   - **Unit of Measure Service**: Supplies UOM code mappings.  
2. **Caching**:  
   - UOM data is cached in-memory after the first request to reduce latency.  
   - Cache can be manually purged via `purgeUomToCodeCache()`.  
3. **Authentication**: Requires a valid `AzureToken` for external API calls.  

---

### **Assumptions & Notes**  
- **Caching Strategy**:  
  - UOM data is cached indefinitely; refreshes require manual cache clearance.  
  - Default UOM fetch limit: 500 records.  
- **Pagination**:  
  - Default item limit: 500 (configurable via `limit` parameter).  
- **Performance**:  
  - Combining item data with cached UOM codes minimizes external API calls.  
  - Long item numbers improve filtering accuracy.  

---

**Revision History**  
- v1.0 | [Date] | Initial Document.