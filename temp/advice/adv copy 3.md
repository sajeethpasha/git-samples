# Global Exception Handling Documentation

## Overview
The `GlobalExceptionHandler` class is a centralized exception handling mechanism implemented in a Spring Boot application. It ensures consistent error responses across all controllers by catching exceptions and translating them into standardized `ApiError` objects with appropriate HTTP status codes. This approach improves maintainability and provides a unified structure for client applications to handle errors.

## Key Features
- **Centralized Exception Handling**: All exceptions are processed in one location.
- **Customized Error Messages**: Context-specific details included in error responses.
- **HTTP Status Codes**: Proper status codes (400, 404, 500 etc.) for different error types.
- **Request Object Logging**: Serializes failed request payloads for debugging.

## Exception Handling Process
![Global Exception Handling Flow](https://example.com/exception-handling-flow.png)

### 1. Exception Detection
- Annotated with `@ControllerAdvice` to listen for exceptions across all controllers
- Uses `@ExceptionHandler` with 50+ specific exception classes

### 2. Error Processing
```java
// Example handling flow
if (ex instanceof GroupNotFoundException) {
  return handleGroupNotFoundException(...);
} else if (ex instanceof GroupCreationException) {
  return handleGroupCreationException(...);
} // ... other exceptions
```

### 3. Response Generation
Produces `ApiError` response body with:
```json
{
  "errorMessages": [
    "Group not found",
    "Group id searched for: 12345"
  ]
}
```

## Error Response Structure
| Field          | Type         | Description                          |
|----------------|--------------|--------------------------------------|
| errorMessages  | List<String> | Human-readable error details         |

## Common Exception Types
| Exception Class                          | HTTP Status | Example Response                      |
|------------------------------------------|-------------|----------------------------------------|
| `GroupNotFoundException`                 | 404 Not Found | Group not found, Group id: 12345     |
| `InvalidArgumentsException`              | 400 Bad Request | Invalid request parameters          |
| `WorkOrderCreationException`             | 400 Bad Request | Serialized create request payload   |
| `ItemConversionNotFoundException`        | 404 Not Found | Item not found, Item id: ABC-123    |
| `AttachmentDownloadException`            | 400 Bad Request | File download failed               |

## Implementation Details

### 1. Serialization Helper
```java
private String TrySerializeObject(Object obj) {
  try {
    return mapper.writeValueAsString(obj);
  } catch (Exception ex) {
    return obj.toString(); // Fallback
  }
}
```
- Used to log request payloads in error messages
- Handles serialization failures gracefully

### 2. Custom Error Enrichment
Example for `WorkOrderMaterialUpdateException`:
```java
errorMessages.add("Work Order Id: " + ex.getWorkOrderId());
errorMessages.add("Materials: " + TrySerializeObject(ex.getMaterials()));
```

## Adding New Exceptions
1. Add to `@ExceptionHandler` annotation list
2. Create new handler method:
```java
private ResponseEntity<ApiError> handleNewException(NewException ex, 
                                                   HttpStatus status) {
  List<String> errors = new ArrayList<>();
  errors.add(ex.getMessage());
  return new ResponseEntity<>(new ApiError(errors), status);
}
```

## Best Practices
1. **Use Specific Exceptions**: Create domain-specific exceptions
2. **Include Context**: Add relevant IDs/parameters to error messages
3. **Secure Sensitive Data**: Avoid serializing confidential information
4. **Monitor 500 Errors**: Generic exceptions should be investigated

## Troubleshooting Guide
| Symptom                      | Possible Cause                | Solution                              |
|------------------------------|-------------------------------|---------------------------------------|
| Empty errorMessages          | Exception with null message   | Add explicit message in exception     |
| Serialization failure        | Circular object references    | Implement custom toString() methods   |
| Incorrect status code        | Wrong HTTPStatus in handler   | Verify status mapping in @ExceptionHandler |

This documentation should be updated whenever new exception types are added or error handling logic changes.