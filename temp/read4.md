(Due to technical issues, the search service is temporarily unavailable.)

**Functional Flow Document: Blend Lot Inventory Transaction Process**

---

### **1. Overview**
This document outlines the functional flow of the Blend Lot Inventory Transaction process. The process involves two main operations: **BlendOut** (issuing inventory from a source lot) and **BlendIn** (receiving inventory into a destination lot). The system ensures inventory balance is maintained by adjusting quantities and transaction types based on the sign of the transaction quantity.

---

### **2. Key Components**
- **API Endpoint**: `POST /blendLot`
- **Input**: `BlendLotCreateRequest` (includes details like organization ID, item number, quantities, lot/subinventory details, etc.).
- **Output**: 
  - Success (`HTTP 201`): Returns `true` if both transactions succeed.
  - Failure (`HTTP 400`): Returns error details if either transaction fails.

---

### **3. High-Level Flow**
1. **API Trigger**  
   A client sends a `POST` request to the `/blendLot` endpoint with a `BlendLotCreateRequest` payload.

2. **Process BlendOut Transaction**  
   - Deduct inventory from the **source lot** (e.g., "From Lot").
   - Transaction type (`Issue` or `Receipt`) and quantity are determined by the sign of `transactionQuantity`:
     - **Positive Quantity**: `Account Alias Issue` (deduct from source).
     - **Negative Quantity**: Convert to `Account Alias Receipt` (reverse deduction, absolute value used).

3. **Process BlendIn Transaction**  
   - Add inventory to the **destination lot** (e.g., "To Lot").
   - Transaction type (`Receipt` or `Issue`) mirrors the BlendOut logic:
     - **Positive Quantity**: `Account Alias Receipt` (add to destination).
     - **Negative Quantity**: Convert to `Account Alias Issue` (reverse addition).

4. **Error Handling**  
   - If either transaction fails, the process stops and returns an error.
   - Errors from external inventory service calls are propagated back to the client.

---

### **4. Detailed Flow**

#### **Step 1: Parse Input and Adjust Timestamp**
- Extract fields from `BlendLotCreateRequest` (e.g., `organizationId`, `itemNumber`, `transactionQuantity`).
- Adjust the `transactionDate` to UTC time minus 1 minute (to align with system requirements).

#### **Step 2: Generate BlendOut Transaction**
- **Build BlendOut Request**:  
  Uses `getAccountAliasIssueTransactionForBlendLotOut` to create a transaction with:
  - **Source lot details** (subinventory, locator, lot number).
  - Transaction type: `Account Alias Issue` (default) or `Account Alias Receipt` (if quantity is negative).
  - Quantity: Adjusted to negative for deduction (e.g., `-transactionQuantity`).

- **Call External Inventory Service**:  
  Sends the request to the external service via `postInventoryTransaction`.

#### **Step 3: Validate BlendOut Response**
- If successful, proceed to BlendIn.
- If failed, throw an error (e.g., `InventoryTransactionCreationException`).

#### **Step 4: Generate BlendIn Transaction**
- **Build BlendIn Request**:  
  Uses `getAccountAliasReceiptTransactionForBlendLotIn` to create a transaction with:
  - **Destination lot details** (subinventory, locator, lot number).
  - Transaction type: `Account Alias Receipt` (default) or `Account Alias Issue` (if quantity is negative).
  - Quantity: Matches the original `transactionQuantity`.

- **Call External Inventory Service**:  
  Sends the request to the external service via `postInventoryTransaction`.

#### **Step 5: Validate BlendIn Response**
- If successful, return `true`.
- If failed, throw an error.

---

### **5. Transaction Type Logic**
| Scenario | BlendOut Transaction Type | BlendIn Transaction Type |  
|----------|----------------------------|---------------------------|  
| `transactionQuantity > 0` | `Account Alias Issue` (deduct from source) | `Account Alias Receipt` (add to destination) |  
| `transactionQuantity < 0` | `Account Alias Receipt` (reverse deduction) | `Account Alias Issue` (reverse addition) |  

---

### **6. Error Scenarios**
1. **Invalid Input**: Missing fields or invalid data in `BlendLotCreateRequest`.
2. **External Service Failure**:  
   - 4xx/5xx errors from inventory service (e.g., invalid authorization, validation issues).
   - Errors are wrapped in `InventoryDomainException` or `InventoryTransactionCreationException`.
3. **Logical Errors**:  
   - Negative quantity handling (e.g., unexpected sign reversal).
   - Lot/subinventory mismatches.

---

### **7. Integration Points**
- **External Inventory Service**:  
  - Endpoint: `POST /transaction` (handles inventory adjustments).
  - Headers: Requires `Authorization` token (Azure AD token).
  - Payload: `InventoryStagedTransactionCreateRequest`.

---

### **8. Key Data Elements**
| Field | Description |  
|-------|-------------|  
| `transactionQuantity` | Quantity to transfer; sign determines transaction type. |  
| `transactionDate` | Adjusted to UTC time minus 1 minute. |  
| `fromLotSubinventoryCode` / `toLotSubinventoryCode` | Source/destination subinventory codes. |  
| `fromLotNumber` / `toLotNumber` | Source/destination lot numbers. |  

---

### **9. Flow Diagram**
```plaintext
[Client] 
  → POST /blendLot (BlendLotCreateRequest) 
  → [Service: BlendOut Transaction] 
    → External Inventory Service 
  → [Service: BlendIn Transaction] 
    → External Inventory Service 
  → Return Success (true) or Error
```

---

### **10. Management Summary**
This process ensures accurate inventory adjustments for blending operations by:
1. Dynamically switching transaction types based on quantity sign.
2. Validating inputs and external service responses rigorously.
3. Maintaining auditability through transaction details (e.g., source/destination lots, timestamps).  
**Business Impact**: Enables seamless inventory transfers between lots while adhering to accounting and tracking requirements.