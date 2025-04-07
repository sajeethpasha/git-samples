# AOP Logging Implementation Documentation

## Overview
This document outlines the implementation of Aspect-Oriented Programming (AOP) for centralized logging in the `pmx-wbm-bff-ms` application. The solution enhances traceability, debugging efficiency, and consistency in log management across the application.

---

## Key Features
1. **Unified Logging**  
   - Logs method entry/exit, arguments, errors, and exception handler responses.
   - Eliminates repetitive logging code in business logic.

2. **Trace ID Correlation**  
   - Integrates with OpenTelemetry to inject `traceId` into logs for distributed tracing.

3. **Exception Handling Insights**  
   - Logs details when global exception handlers process errors.

4. **Internationalization Support**  
   - Uses `MessageSource` for locale-specific log messages (ready for future i18n/l10n).

---

## Technical Implementation

### 1. Components
| Component                 | Purpose                                                                 |
|---------------------------|-------------------------------------------------------------------------|
| `LoggingAspect`           | AOP aspect defining logging behavior around methods & exception handlers|
| `CustomLoggingConfig`     | Configures message sources for log messages                             |
| `loggingMessages.properties` | Contains parameterized log messages (e.g., method entry/exit templates) |

### 2. Logging Behavior
#### **Method Execution Logging**  
- **Applies To**: All methods in `com.sherwin.edps.batchmgmtbff` *except*:
  - Configuration classes (`..configuration..`)
  - Client classes (`..clients..`)
  
- **What's Logged**:
  - Method entry with arguments
  - Method exit
  - Errors (including stack traces)
  - OpenTelemetry `traceId` for correlation

#### **Exception Handler Logging**  
- **Applies To**: `GlobalExceptionHandler` methods
- **What's Logged**:
  - Exception details when handlers are invoked
  - Response returned by handlers

### 3. Flow Diagram
```
[Method Call] → LoggingAspect
                   ├──▶ Add traceId to logs (MDC)
                   ├──▶ Log method entry + args
                   ├──▶ Execute method
                   ├──▶ Log exit/success
                   └──▶ On error: Log exception + stack trace
```

---

## Dependencies
| Dependency                     | Purpose                          |
|--------------------------------|----------------------------------|
| `spring-boot-starter-aop`      | Enables Spring AOP support       |
| `aspectjweaver`                | AspectJ integration              |
| `opentelemetry-api`            | Trace ID extraction              |

---

## Configuration
### `CustomLoggingConfig.java`
- Defines `customMessageSource` bean pointing to `loggingMessages.properties`.
- Supports UTF-8 encoding for log messages.

### `loggingMessages.properties`
```properties
log.method.entry=Entering method {0}
log.method.exit=Exiting method {0}
log.method.error=Error in method {0}: {1}
```

---

## Exclusions & Optimizations
- **Excluded Packages**:  
  - Configuration and client classes to avoid logging framework internals.
- **MDC Cleanup**:  
  `traceId` is cleared from logging context (`MDC.clear()`) post-execution.
- **Performance**:  
  Uses `@Lazy` initialization for `MessageSource` to avoid circular dependencies.

---

## Benefits
- **Debugging**: Full visibility into method flow and exceptions.
- **Observability**: `traceId` links logs across microservices.
- **Maintainability**: Logging logic centralized; no code duplication.
- **Compliance**: Ready for audit requirements with argument/error logging.

---

## How to Use
1. **Add Logging**  
   Simply add methods to monitored packages – logging happens automatically.

2. **View Logs**  
   Search logs using `traceId` to follow a request across services.

3. **Customize Messages**  
   Modify `loggingMessages.properties` to update log templates.

---

## Future Enhancements
1. Add log severity levels (e.g., WARN for business errors).
2. Include user/tenant context in logs.
3. Extend i18n support with locale-specific message files.

---

**Revision History**  
- 1.0.0 | MM/DD/YYYY | Initial Implementation