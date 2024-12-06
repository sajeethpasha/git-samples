# Documentation: `generateErpIntegrations` Method

## Overview
The `generateErpIntegrations` method is responsible for generating ERP integrations based on the provided request body, which contains details of ERP integration records. This process involves generating ERP integrations through an external API client, updating work orders based on the integration response, and handling different cases based on the response status. The method is implemented using reactive programming with Project Reactor's `Mono` and `Flux` classes.

## Method Signature
```java
public Mono<ErpIntegrationsResponse> generateErpIntegrations(ErpIntegrationsRequestBody body)
```

### Parameters
- **`body`** (`ErpIntegrationsRequestBody`): The request body containing a list of ERP integrations to be created.

### Return Type
- **`Mono<ErpIntegrationsResponse>`**: A reactive stream that will eventually emit an `ErpIntegrationsResponse` or an error.

## Process Flow
1. **Extract ERP Integrations List**: Retrieve the list of ERP integration requests from the request body.
   ```java
   List<ErpIntegrationsCreateRequest> erpIntegrationsList = body.getErpIntegrations();
   ```

2. **Generate ERP Integrations**: Use the external `client` to generate the ERP integrations by invoking the `generateErpIntegrations` method, passing the extracted list.
   ```java
   return client.generateErpIntegrations(erpIntegrationsList)
   ```

3. **Logging and Error Handling**:
   - Log a success message upon successful generation.
   - Log an error message if the process fails.
   ```java
   .doOnSuccess(response -> logger.info("ERP integrations generated successfully"))
   .doOnError(error -> logger.error("Error generating ERP integrations", error))
   ```

4. **Update Work Orders**:
   - For each integration in the original request list, find the corresponding integration in the response.
   - If a matching integration is found, create a `WorkOrderUpdateRequest` to update the work order with new information.
   - Invoke `updateWorkOrder` to update the relevant work order using the client.
   - Log success or error messages accordingly.
   ```java
   .flatMap(erpIntegrationsResponse -> Flux.fromIterable(erpIntegrationsList)
           .flatMap(integration -> {
               Optional<ErpIntegration> optionalErpIntegration = erpIntegrationsResponse.getErpIntegrations().stream()
                       .filter(erpIntegration -> erpIntegration.getWorkOrderId().equals(integration.getWorkOrderId()))
                       .findFirst();

               if (optionalErpIntegration.isPresent()) {
                   WorkOrderUpdateRequest workOrderUpdateRequest = WorkOrderUpdateRequest.builder()
                           .requestId(requestId)
                           .batchValue(integration.getValue())
                           .build();
                   return updateWorkOrder(workOrderUpdateRequest, workOrderId)
                           .doOnSuccess(response -> logger.info("Work order updated successfully for workOrderId: " + workOrderId))
                           .doOnError(error -> logger.error("Error updating work order for workOrderId: " + workOrderId, error));
               } else {
                   logger.info("No matching integration found for work order number: {}", integration.getOrderNumber());
                   return Mono.error(new ErpIntegrationsException("No matching integration found for work order number " + integration.getOrderNumber()));
               }
           }).then(Mono.just(erpIntegrationsResponse)))
   ```

5. **Top-Level Error Handling**: Log any errors that occur during the entire ERP integration process.
   ```java
   .doOnError(exception -> logger.error("Error creating ERP integration {}", exception.getMessage()));
   ```

## Supporting Methods

### `generateErpIntegrationsCurlCommand`
This private method generates a cURL command for creating ERP integrations.

**Method Signature**:
```java
private String generateErpIntegrationsCurlCommand(List<ErpIntegrationsCreateRequest> body)
```

- **Parameters**: `List<ErpIntegrationsCreateRequest>`: List of ERP integration requests.
- **Return Type**: `String` containing the generated cURL command.

The method constructs the cURL command using the base URL, Azure token, and JSON representation of the request body.

### `generateErpIntegrations`
This method sends a POST request to the `/erp-integrations` endpoint using the provided ERP integration requests.

**Method Signature**:
```java
public Mono<ErpIntegrationsResponse> generateErpIntegrations(List<ErpIntegrationsCreateRequest> body)
```

- **Parameters**: `List<ErpIntegrationsCreateRequest>`: List of ERP integration requests to be sent.
- **Return Type**: `Mono<ErpIntegrationsResponse>` containing the response from the ERP service.

### `updateWorkOrder`
This method updates a work order based on the provided `WorkOrderUpdateRequest`.

**Method Signature**:
```java
public Mono<WorkOrder> updateWorkOrder(WorkOrderUpdateRequest workOrderUpdateRequest, String workOrderId)
```

- **Parameters**:
  - `workOrderUpdateRequest` (`WorkOrderUpdateRequest`): Request object containing update details for the work order.
  - `workOrderId` (`String`): The identifier for the work order to be updated.
- **Return Type**: `Mono<WorkOrder>` containing the updated work order details.

## Error Handling
The `generateErpIntegrations` method handles different types of errors:
1. **ERP Integration Generation Error**: Logged with the message "Error generating ERP integrations".
2. **Work Order Update Error**: Logged for specific work order IDs with messages like "Error updating work order for workOrderId".
3. **No Matching Integration Found**: If no corresponding integration is found in the response, an error is returned with the message "No matching integration found for work order number".
4. **Top-Level Error**: Any error during the entire process is logged with "Error creating ERP integration".

## Logging
The method logs different stages of execution to aid in troubleshooting and tracking:
- Successful generation of ERP integrations.
- Errors during ERP integration generation.
- Successful updates to work orders.
- Errors during work order updates.
- Missing integrations for specific work orders.

## cURL Commands
Both `generateErpIntegrations` and `updateWorkOrder` have methods to generate corresponding cURL commands, useful for debugging or testing purposes.

- Example cURL command for generating ERP integrations:
  ```
  curl -X POST 'https://example.com/erp-integrations' \
       -H 'Content-Type: application/json' \
       -H 'Authorization: Bearer <AzureToken>' \
       -d '<RequestBody>'
  ```
- Example cURL command for updating a work order:
  ```
  curl -X PATCH 'https://example.com/work-orders/{workOrderId}' \
       -H 'Content-Type: application/json' \
       -H 'Authorization: Bearer <AzureToken>' \
       -d '<WorkOrderUpdateRequest>'
  ```

## Summary
The `generateErpIntegrations` method provides a robust mechanism for handling ERP integrations and subsequent work order updates. It employs reactive programming techniques to efficiently handle multiple asynchronous operations, ensuring each step of the process is logged and any errors are appropriately managed. The method facilitates traceability through detailed logging, and provides useful cURL commands to assist with manual testing and debugging.

