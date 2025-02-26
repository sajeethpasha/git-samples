(Due to technical issues, the search service is temporarily unavailable.)

**Documentation for Management: Update Lot Attribute DFFs Flow**

---

### **Overview**
This document explains the high-level flow of the "Update Lot Attribute DFFs" operation, which allows modifying Descriptive Flexfield (DFF) attributes associated with a specific lot in the system. The process involves multiple steps, including API handling, service-to-service communication, and interactions with an Oracle database.

---

### **Key Components**
1. **API Endpoint**:  
   - **PATCH /attributeDffs/{lot-number}**: Updates DFF attributes for a lot identified by `lot-number`.
   - Input: `lotNumber` (path parameter) and request body (`LotStandardDFFUpdateRequestDTO`).
   - Output: Updated DFF attributes (`StandardLotAttributeDffs`).

2. **Layers Involved**:
   - **API Controller**: Handles incoming HTTP requests.
   - **Service Layer**: Orchestrates business logic and communicates with downstream services.
   - **Domain Service**: Manages database interactions (Oracle) and hypermedia-driven navigation.
   - **Client Layer**: Facilitates HTTP communication between services.

---

### **High-Level Flow**
#### **1. API Request Handling**
- **Step 1**: A client (e.g., frontend or external system) sends a **PATCH** request to update DFF attributes for a lot.  
  Example:  
  `PATCH /attributeDffs/LOT123`  
  `Body: { ... }`

- **Step 2**: The API Controller (`updateLotAttributeDffs` method) receives the request, extracts the `lotNumber` and request body, and delegates processing to the **Service Layer**.

---

#### **2. Service-to-Service Communication**
- **Step 3**: The Service Layer uses a **WebClient** to forward the request to an internal domain service (or external system).  
  - The request includes:  
    - Target URI: `/attributeDffs/{lotNumber}` (configured dynamically).  
    - Authorization token (e.g., Azure AD token).  
    - Request body.  

- **Step 4**: The domain service processes the request using the `patchLotAttributeDffs` method.  

---

#### **3. Domain Service Logic**
- **Step 5**: Fetch initial lot data from Oracle using `lotNumber`:  
  - A query is executed to retrieve the lot’s metadata, including hypermedia links (HATEOAS).  
  - Example link extracted: `inventoryItemLots/INV123` (used to identify the lot’s unique inventory ID).

- **Step 6**: Retrieve DFF attributes linked to the lot:  
  - Use the `inventoryUniqId` (from Step 5) to fetch DFF metadata.  
  - Extract the `lotAttributeDFF` hypermedia link to identify the DFF’s unique ID (e.g., `lotAttributeDFF/DFF456`).

- **Step 7**: Update DFF attributes in Oracle:  
  - Execute a **PATCH** operation on the identified DFF resource (`DFF456`) using the request body.  

---

#### **4. Response Flow**
- **Step 8**: The domain service returns the updated DFF attributes to the WebClient.  
- **Step 9**: The WebClient forwards the response to the original API Controller.  
- **Step 10**: The API Controller sends the final response (HTTP 200 or 404) back to the client.  

---

### **Key Technical Notes**
1. **Hypermedia (HATEOAS)**:  
   - The system relies on embedded links in responses (e.g., `inventoryItemLots`, `lotAttributeDFF`) to navigate between resources. This decouples clients from hardcoded URIs.

2. **Oracle Integration**:  
   - Queries use `OracleTypeDefinition` and `OracleQueryParameters` to interact with Oracle databases.  
   - Domain services dynamically generate SQL clauses (e.g., `lotNumber = 'LOT123'`).

3. **Security**:  
   - Internal/external service calls use tokens (e.g., `AzureToken` in headers) for authentication.  
   - The `getDomainService` method enforces security policies (e.g., JWT vs. basic auth).

4. **Reactive Programming**:  
   - The system uses **Project Reactor** (e.g., `Mono`) for non-blocking, asynchronous operations.

---

### **Error Handling**
- **HTTP 404**: Returned if the lot or DFF resource does not exist.  
- **4xx/5xx Errors**: Converted to `InventoryDomainException` with details from `ApiError`.  
- **Logging**: Critical steps (e.g., request/response payloads) are logged for auditing.

---

### **Visual Flow Diagram**
```
Client → API Controller → WebClient → Domain Service → Oracle DB  
          (PATCH)          (Forward Request)   (Fetch/Patch Data)  
          ↑                                      ↓  
          ←─────────── Return Response ←─────────
```

---

### **Management Takeaways**
- The process emphasizes **modularity** (APIs, services, domain logic) and **scalability** (reactive design).  
- **Hypermedia-driven navigation** ensures flexibility in URI changes without client impact.  
- Security and data integrity are prioritized via token-based auth and Oracle transactions.  
- Logging and error handling provide visibility into system behavior and failures.