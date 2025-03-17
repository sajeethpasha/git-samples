Below is a management-oriented document that summarizes the purpose, functionality, and key processes of the "Onhand Quantity Details" feature. This overview is intended to help non-technical stakeholders understand how the system retrieves and processes onhand quantity details, and how it integrates various data sources to support business operations.

---

# Management Overview Document: Onhand Quantity Details Retrieval

## 1. Purpose and Functionality

The primary goal of this component is to retrieve comprehensive onhand quantity details for inventory items. It serves as a critical link between real-time inventory data and business reporting needs, ensuring that accurate and up-to-date inventory information is available for decision-making processes. 

The endpoint accepts key parameters such as:
- **Organization Code** – Identifies the organization associated with the inventory.
- **Subinventory Code (optional)** – Further refines the query to a specific location or sub-section of the inventory.
- **Item Number (optional)** – Specifies the inventory item for which onhand details are being queried.

## 2. Key Features and Workflow

### A. API Endpoint Exposure

- **Endpoint URL:** `/inv/onHandQtyDtls`
- **Method:** GET
- **Response Format:** The endpoint returns a reactive data structure encapsulated in a list of `InventoryOnhandDetails` objects.
- **Response Codes:**  
  - **200 OK:** Onhand quantity details retrieved successfully.
  - **404 Not Found:** Details not available for the provided query parameters.

### B. Data Retrieval Process

1. **Sanitization:**  
   Before processing, the input parameters (organization code, subinventory code, and item number) are sanitized to prevent potential issues such as injection attacks or malformed queries.

2. **Initial Data Fetch:**  
   The service first calls an internal client to fetch the raw onhand details from a BI (Business Intelligence) proxy service.  
   - If the returned dataset is empty, the system logs the absence of data and returns an empty list.
   - If data is found, it proceeds to the next step.

3. **Additional Data Enrichment via SOAP API:**  
   For each record retrieved:
   - The system calls a secondary SOAP-based API to obtain reservation details for the item.
   - Upon receiving a valid response, key attributes such as `AvailableToTransact`, `AvailableToReserve`, and `OnhandQuantity` are updated in the original record.
   - In case of an error during this call, the system captures the exception and continues processing without halting the overall workflow.

4. **Data Transformation:**  
   The service further refines the data:
   - Adjusts locator values by replacing hyphens with periods to ensure consistency in data format.
   - Collects all enriched records into a final list that is returned as the response.

### C. Error Handling and Resilience

- **Graceful Degradation:**  
  The design ensures that if the SOAP API call fails for any record, the system does not stop processing the remaining items. Instead, it logs the error and proceeds with the available data.
  
- **Detailed Logging:**  
  All significant steps, including data retrieval from both the BI proxy and SOAP API, are logged. This supports troubleshooting and helps maintain traceability.

- **Exception Mapping:**  
  Any errors occurring during the overall process are captured and mapped to a custom exception (`ListInventoryOnhandDetailsNotFoundException`). This standardizes error responses and ensures consistent communication with clients.

## 3. Security and Performance Considerations

- **Authorization:**  
  Every call to external inventory or SOAP services includes an `Authorization` header, ensuring that only authenticated requests are processed. This protects sensitive inventory data from unauthorized access.

- **Reactive and Asynchronous Processing:**  
  The system leverages reactive programming (using `Mono` and `Flux`) for asynchronous processing, which optimizes resource utilization and improves performance under high-load scenarios.

- **Resilience and Monitoring:**  
  Through comprehensive logging and error handling, the system is designed to be resilient. Continuous monitoring of log entries helps quickly identify and address any issues in real time.

## 4. Business Impact

- **Improved Decision-Making:**  
  By providing accurate and timely onhand inventory details, the system supports better stock management, reservation planning, and overall operational efficiency.
  
- **Customer Satisfaction:**  
  Accurate inventory data minimizes the risk of overbooking or stock discrepancies, directly contributing to improved customer satisfaction.

- **Operational Efficiency:**  
  The integration of multiple data sources (BI proxy and SOAP API) ensures that decision-makers have a complete view of the inventory status, streamlining reporting and analysis processes.

---

This document outlines the strategic importance and operational details of the onhand quantity details retrieval feature, providing a clear picture of how it contributes to overall inventory management and business efficiency.