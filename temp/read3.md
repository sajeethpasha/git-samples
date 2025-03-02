Below is a functional flow document that explains the overall process in a management-friendly language. This document outlines how the BlendIn/BlendOut Lot Inventory Transaction is handled from the REST endpoint to the downstream service calls and error handling.

---

## Functional Flow Document: BlendIn and BlendOut Lot Inventory Transaction

### 1. **Overview**

This service manages the transfer (blend) of inventory lots between two locations. The transaction is divided into two parts:
- **BlendOut (Issue):** Removing inventory from the source lot.
- **BlendIn (Receipt):** Adding inventory to the target lot.

The process ensures that the right inventory adjustments are made based on whether the transaction quantity is positive or negative, and it handles any errors that might occur during the call to the inventory system.

---

### 2. **REST API Endpoint**

- **Endpoint:** `/blendLot`  
- **Method:** POST  
- **Purpose:** Initiate the blend transaction by receiving a JSON payload (containing details such as organization, item, quantity, lot numbers, etc.).  
- **Response:** Returns a `Mono<Boolean>` wrapped in a ResponseEntity with HTTP status 201 (Created) when the operation is successful.

**Key Step:**
- The API controller receives the request and logs the final transaction response.

---

### 3. **Service Method: `blendLotInventoryTransaction`**

#### **Input Extraction and Preprocessing**

- The method extracts the required values (organization code, IDs, item details, transaction quantity, and dates) from the incoming request.
- It processes the transaction date (removing any millisecond component and adjusting the time by subtracting one minute).
- It sanitizes locator names by replacing periods with hyphens.

#### **Branching Based on Transaction Quantity**

The method creates the inventory transaction request using the service:
- **When Transaction Quantity is Positive:**
  - It sets up an **"Account Alias Issue"** request for the blend out operation.
  - It then calls the inventory service to post the transaction.
- **When Transaction Quantity is Negative:**
  - It adjusts the quantity to its absolute value.
  - It changes the transaction type to **"Account Alias Receipt"** for the blend out operation.
  - It then calls the inventory service similarly.

#### **Error Handling and Subsequent Flow**

- After the first transaction (blend out) is processed, the method checks the result:
  - **If no error is reported:**  
    It then builds the blend in (receipt) transaction request.  
    - For a positive transaction quantity, it proceeds with the request as an **"Account Alias Receipt"**.  
    - For a negative transaction quantity, it updates the request to **"Account Alias Issue"** and uses the original transaction quantity.
  - **If an error exists:**  
    The process throws an exception with the error explanation, ensuring that the transaction does not proceed silently in case of failure.

- The blend in request is then processed through a helper method (`processError`) that submits the blend in transaction and confirms whether it succeeded or failed.

- **Final Output:**  
  If both operations (blend out and blend in) complete successfully, the method returns `true`; otherwise, it raises an exception that is mapped to an appropriate error response.

---

### 4. **Interaction with the Inventory Service**

The communication with the external inventory service is handled via the `postInventoryTransaction` method:
- **HTTP Request:**  
  The service uses a reactive client (Mono-based) to make a POST call to `/transaction` on the inventory system.
- **Headers and Authentication:**  
  The call includes an Authorization header (with an Azure token from the context).
- **Error Handling:**  
  - **4xx errors:** The response is interpreted as a client error, wrapped into an `InventoryDomainException`.
  - **5xx errors:** The response is interpreted as a server error, and a generic exception is thrown.
- **Response:**  
  Upon successful call, the method logs and returns the inventory transaction response.

---

### 5. **Supporting Utility Methods**

- **Transaction Request Builders:**  
  Two helper methods create the transaction request objects for:
  - **Blend Out (Issue):** Uses the method that builds an "Account Alias Issue" request with negative quantity.
  - **Blend In (Receipt):** Uses the method that builds an "Account Alias Receipt" request.
  
- **Base Request Builder:**  
  A common builder (`getBaseTransactionDetailsForBlendLot`) sets up shared properties (like process status, source line, cost flag, etc.) for both blend in and blend out requests.

- **Error Processing:**  
  The `processError` method submits the blend in request and verifies if the transaction was error-free. If any errors are detected, it throws an exception.

---

### 6. **Summary Flow Diagram**

1. **Client Call:**  
   Client submits a POST request to `/blendLot` with the necessary transaction data.

2. **Controller Layer:**  
   The REST controller receives the request and calls `blendLotInventoryTransaction`.

3. **Data Preparation:**  
   The service extracts parameters, adjusts the transaction date/time, and sanitizes input data.

4. **Blend Out Operation:**  
   - Constructs the blend out request.
   - Decides transaction type based on whether quantity is positive or negative.
   - Calls `postInventoryTransaction` to process the blend out.

5. **Result Evaluation:**  
   - If blend out returns without error, proceed.
   - Otherwise, raise an exception.

6. **Blend In Operation:**  
   - Constructs the blend in request.
   - Adjusts transaction type if needed (reverses the logic from blend out for negative values).
   - Calls `processError` to post the blend in transaction.
   - Evaluates the response; if successful, returns `true`.

7. **Final Response:**  
   The controller returns a `201 Created` response with a Boolean `true` if the entire flow is successful.

---

### 7. **Key Points for Management**

- **Seamless Dual Operation:**  
  The system efficiently handles both the removal (blend out) and addition (blend in) of inventory with a single API call.
  
- **Dynamic Handling:**  
  It supports both positive and negative transaction quantities by dynamically adjusting the request type and quantity.
  
- **Robust Error Handling:**  
  Detailed checks after each transaction ensure that errors are caught early, and meaningful exceptions are raised to prevent partial updates.
  
- **Audit and Logging:**  
  The process includes logging at multiple steps to provide traceability and help with troubleshooting if issues occur.
  
- **Extensible Design:**  
  The use of builders and modular methods allows for easy modifications and enhancements without major changes to the overall flow.

---

This document provides a clear view of the transaction flow from an incoming request to the final posting of both blend out and blend in transactions, ensuring inventory data integrity and error management throughout the process.