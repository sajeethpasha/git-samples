# AOP Logging Implementation Documentation

## Overview

This document provides a detailed explanation of the Aspect-Oriented Programming (AOP) logging implementation in the Work Order Batch Management BFF microservice. The implementation uses Spring AOP to centralize logging across the application, ensuring consistent log formatting, contextual information, and distributed tracing integration.

## Purpose

The AOP logging implementation serves several key purposes:

1. **Standardized Logging**: Ensures all method entries, exits, and exceptions are logged consistently throughout the application.
2. **Distributed Tracing**: Integrates with OpenTelemetry to correlate logs with distributed traces.
3. **Internationalization Support**: Enables localized log messages using Spring's MessageSource.
4. **Centralized Error Handling**: Provides detailed exception logging without polluting business logic with logging code.
5. **Debugging Assistance**: Automatically logs method parameters and exception stack traces to assist troubleshooting.

## Implementation Components

The logging implementation consists of the following components:

### 1. CustomLoggingConfig

This configuration class creates a message source bean that loads logging messages from a resource bundle.

```java
@Configuration
public class CustomLoggingConfig {

    @Bean(name = "customMessageSource")
    public MessageSource customMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:loggingMessages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
```

### 2. LoggingAspect

This aspect class intercepts method calls and logs method entry, exit, and exceptions.

Key features include:

- **Method Entry/Exit Logging**: Records when methods are called and when they complete.
- **Exception Logging**: Captures all exceptions with detailed information including stack traces.
- **OpenTelemetry Integration**: Associates logs with trace IDs for distributed tracing.
- **Internationalization**: Uses message resources for localized logging.
- **Exception Handler Monitoring**: Special handling for GlobalExceptionHandler responses.

### 3. Message Resource Bundle

The `loggingMessages.properties` file contains standardized log message templates:

```properties
log.method.entry=Entering method {0}
log.method.exit=Exiting method {0}
log.method.error=Error in method {0}: {1}
log.method.runtime.error=Runtime exception in method {0}: {1}
```

## Technical Details

### Pointcut Expressions

The implementation uses two main pointcut expressions:

1. **General Method Interception**:
   ```java
   @Around("within(com.sherwin.edps.batchmgmtbff..*) " +
           "&& !within(com.sherwin.edps.batchmgmtbff.configuration..*) " +
           "&& !within(com.sherwin.edps.batchmgmtbff.clients..*)")
   ```
   This expression intercepts all methods within the application package, excluding configuration and client packages to prevent excessive logging.

2. **Exception Handler Interception**:
   ```java
   @Around("execution(* com.sherwin.edps.batchmgmtbff..*GlobalExceptionHandler.*(..))")
   ```
   This expression specifically targets exception handler methods to provide detailed logging of error responses.

### Dependencies

The implementation relies on the following dependencies:

- **Spring AOP**: For aspect implementation (`spring-boot-starter-aop`)
- **AspectJ**: For weaving capabilities (`aspectjweaver`)
- **OpenTelemetry**: For distributed tracing integration (`opentelemetry-api`)
- **Spring Context**: For message internationalization

## Benefits

1. **Separation of Concerns**: Business logic remains clean, free from logging boilerplate code.
2. **Consistent Logging Format**: Uniform log structure across the application.
3. **Enhanced Troubleshooting**: Method parameters and exception details are automatically captured.
4. **Distributed Tracing**: Correlation IDs link logs to trace spans in monitoring tools.
5. **Maintainability**: Logging behavior can be modified centrally without changing business code.

## Usage Example

When a service method is called, the logs would appear as:

```
2024-04-07 10:15:22.456 INFO [traceId=4bf92f3577b34da6a3ce929d0e0e4736] - Entering method BatchService.processBatch(String, Long) with arguments: [batch123, 456]
2024-04-07 10:15:23.789 INFO [traceId=4bf92f3577b34da6a3ce929d0e0e4736] - Exiting method BatchService.processBatch(String, Long)
```

If an exception occurs:

```
2024-04-07 10:15:23.123 ERROR [traceId=4bf92f3577b34da6a3ce929d0e0e4736] - Error in method BatchService.processBatch(String, Long): Batch not found
2024-04-07 10:15:23.124 ERROR [traceId=4bf92f3577b34da6a3ce929d0e0e4736] - Exception stack trace: java.lang.IllegalArgumentException: Batch not found
    at com.sherwin.edps.batchmgmtbff.service.BatchServiceImpl.processBatch(BatchServiceImpl.java:45)
    ...
```

## Configuration Options

The logging behavior can be customized by:

1. Modifying message templates in `loggingMessages.properties`
2. Adjusting pointcut expressions in `LoggingAspect.java` to include/exclude specific packages
3. Adding additional log context in the aspect methods

## Best Practices

1. Keep pointcut expressions targeted to avoid performance impact
2. Ensure message templates are clear and provide sufficient context
3. Consider log level appropriateness (INFO vs DEBUG) for method entry/exit in production