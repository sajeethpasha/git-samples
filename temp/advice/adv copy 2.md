# Global Exception Handler Documentation

## Overview

The `GlobalExceptionHandler` class is a central component in the Batch Management BFF (Backend For Frontend) application that provides a standardized way to handle exceptions across the entire application. This class intercepts exceptions thrown during request processing and transforms them into consistent API responses with appropriate HTTP status codes.

## Purpose

The primary purposes of this exception handler are:

1. **Consistency**: Provide a unified format for all error responses across the application
2. **Meaningful Error Messages**: Transform technical exceptions into user-friendly error messages
3. **Appropriate Status Codes**: Map exceptions to the most appropriate HTTP status codes
4. **Debugging Information**: Include relevant debugging details in the response when applicable

## Implementation Details

### Class Structure

- The class is annotated with `@ControllerAdvice`, making it a global exception handler for all controllers
- It uses an `ObjectMapper` for JSON serialization of error objects
- Exceptions are mapped to different HTTP status codes (primarily `BAD_REQUEST` and `NOT_FOUND`)
- All error responses use the standardized `ApiError` model

### Error Response Format

All error responses follow a consistent format using the `ApiError` model, which contains:
- A list of error messages providing details about what went wrong
- Additional contextual information when available (such as IDs of resources that weren't found)

### Exception Types Handled

The handler manages exceptions related to:

1. **Group Operations**
   - Creation, update, and retrieval of groups

2. **Work Order Management**
   - Creation and updates of work orders
   - Material updates for work orders
   - Transform work orders

3. **Inventory Management**
   - Transactions and reservations
   - On-hand inventory queries
   - Component quantity details

4. **Item-Related Exceptions**
   - Item number validations
   - Item conversions
   - Filler Rex items

5. **Attachment Handling**
   - Creation and downloading of attachments

6. **Integration Exceptions**
   - ERP integration issues

7. **Validation Exceptions**
   - Invalid arguments
   - Domain-specific validation errors

8. **Other Domain-Specific Exceptions**
   - Customer information
   - Lot numbers
   - Sales orders
   - Sub-inventory codes

### HTTP Status Code Mapping

The handler maps exceptions to the following HTTP status codes:

- **400 BAD_REQUEST**: Used for validation errors, creation failures, and update failures
- **404 NOT_FOUND**: Used when requested resources cannot be found
- **500 INTERNAL_SERVER_ERROR**: Used as a fallback for unhandled exceptions

## Usage Guidelines

### For Developers

1. **Creating New Exception Types**:
   - Create a new exception class extending `RuntimeException`
   - Add the exception to the list in the `@ExceptionHandler` annotation in `GlobalExceptionHandler`
   - Implement a specific handler method for the new exception type

2. **Throwing Exceptions**:
   - Use the most specific exception type available
   - Include meaningful error messages
   - Include contextual information (IDs, request data) when applicable

3. **Exception Inheritance**:
   - Consider creating exception hierarchies for related errors
   - Base exceptions can handle common error patterns

### For API Consumers

1. **Error Response Handling**:
   - Check the HTTP status code to determine the category of error
   - Parse the error messages array for specific details
   - Use specific error details for troubleshooting

2. **Common Error Scenarios**:
   - `400 BAD_REQUEST`: Invalid input parameters or business rule violations
   - `404 NOT_FOUND`: Requested resource does not exist
   - `500 INTERNAL_SERVER_ERROR`: Unexpected server-side issues

## Example Error Responses

### Resource Not Found
```json
{
  "errorMessages": [
    "Group not found",
    "Group id searched for: 12345"
  ]
}
```

### Validation Error
```json
{
  "errorMessages": [
    "Invalid item number length",
    "Item numbers must be between 1 and 40 characters"
  ]
}
```

### Creation Error
```json
{
  "errorMessages": [
    "Failed to create work order",
    "{\"workOrderId\":\"WO-12345\",\"status\":\"PENDING\",...}"
  ]
}
```

## Benefits of Centralized Exception Handling

1. **Maintainability**: Single point of change for error handling logic
2. **Consistency**: Uniform error response format across the application
3. **Separation of Concerns**: Controllers focus on business logic while exception handling is centralized
4. **Better Debugging**: Enhanced error information helps troubleshoot issues faster

## Future Improvements

1. **Localization**: Support for internationalized error messages
2. **Error Codes**: Addition of specific error codes for programmatic handling by clients
3. **Error Logging**: Integration with centralized logging systems
4. **Custom Response Headers**: Adding headers with tracking IDs for support purposes