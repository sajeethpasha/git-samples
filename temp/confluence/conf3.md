Here's a Confluence-style documentation for your AOP logging implementation:

---
# AOP Logging Implementation

## Overview
This documentation describes the centralized logging solution implemented using Aspect-Oriented Programming (AOP) to provide consistent logging patterns, error tracking, and traceability across the application.

![AOP Logging Architecture](https://miro.medium.com/v2/resize:fit:1400/1*F4rN8OBZx9MqOMmkgZ3QYg.png)  
*(Conceptual diagram of AOP logging flow)*

## Key Features
- **Automatic method entry/exit logging**
- **Error handling with stack traces**
- **Trace ID correlation using OpenTelemetry**
- **Exception handler monitoring**
- **I18n-ready log messages**
- **Sensitive package exclusions**

## Components

### 1. Logging Aspect (`LoggingAspect.java`)
**Purpose**: Central controller for logging logic

```java
@Around("within(com.sherwin.edps.batchmgmtbff..*) && !(configuration/clients)")
public Object logAround(ProceedingJoinPoint joinPoint) {
    // Logging implementation
}
```

**Key Functionality**:
- Tracks method execution time
- Logs input parameters
- Captures return values
- Handles exceptions uniformly
- Integrates with OpenTelemetry tracing

### 2. Message Configuration (`CustomLoggingConfig.java`)
**Purpose**: Manages internationalized log messages

```properties
# loggingMessages.properties
log.method.entry=Entering method {0}
log.method.error=Error in method {0}: {1}
```

### 3. Dependency Management (`pom.xml`)
**Essential Dependencies**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-api</artifactId>
</dependency>
```

## Logging Details

### What Gets Logged?
| Event Type | Log Level | Information Captured |
|------------|-----------|----------------------|
| Method Entry | INFO | Method name, arguments |
| Method Exit | INFO | Method name, execution time |
| Errors | ERROR | Exception details, stack trace |
| Exception Handling | INFO | Handler response, exception details |

### Package Exclusions
```java
!within(com.sherwin.edps.batchmgmtbff.configuration..*)
!within(com.sherwin.edps.batchmgmtbff.clients..*)
```

## Trace ID Implementation
```java
// OpenTelemetry integration
MDC.put("traceId", Span.current().getSpanContext().getTraceId());
```

**Benefits**:
- Correlates logs across microservices
- Enables distributed tracing
- Provides request lifecycle visibility

## Error Handling
```java
@Around("execution(* ..GlobalExceptionHandler.*(..))")
public Object logExceptionHandlerResponse() {
    // Exception handler logging
}
```

**Captures**:
1. Exception type
2. Handler method used
3. Final response generated
4. Stack trace details

## Configuration Details

### Message Source Setup
```java
@Bean(name = "customMessageSource")
public MessageSource customMessageSource() {
    ReloadableResourceBundleMessageSource messageSource = 
        new ReloadableResourceBundleMessageSource();
    messageSource.setBasename("classpath:loggingMessages");
    return messageSource;
}
```

### Pointcut Expressions
| Target | Expression |
|--------|------------|
| Main Application | `within(com.sherwin.edps.batchmgmtbff..*)` |
| Exception Handlers | `execution(* ..GlobalExceptionHandler.*(..))` |

## Usage Examples

### Sample Log Outputs
```
INFO  [traceId=abc123] Entering method BatchService.create() with arguments: [BatchRequest(...)]
ERROR [traceId=abc123] Error in method BatchService.create(): Validation failed
INFO  [traceId=abc123] Exception handler response: ErrorDTO(400, "Invalid input")
```

## Best Practices

1. **Exclusion Guidelines**:
   - Avoid logging in configuration classes
   - Exclude external client implementations
   - Skip DTO/model classes

2. **Performance Considerations**:
   - Minimal logging in high-frequency methods
   - Use DEBUG level for verbose logging
   - Asynchronous logging implementation

3. **Security**:
   - Sanitize sensitive data in arguments
   - Avoid logging credentials/PII
   - Use MDC cleanup in finally blocks

## Future Enhancements

1. **Dynamic Log Levels**
2. **Performance Metrics Collection**
3. **Custom Log Appenders**
4. **Alerting Integration**
5. **Audit Logging Support**

---

**Last Updated**: [Insert Date]  
**Owner**: [Your Team Name]  
**Review Cycle**: Quarterly