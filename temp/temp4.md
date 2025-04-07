Here's a clean and clear **documentation** for your AOP-based error logging implementation using Spring AOP, OpenTelemetry, and internationalized message support.

---

## 📘 **AOP Logging Implementation Documentation**

### 🔧 **Purpose**
This AOP-based logging module intercepts method executions within the application (excluding configurations and clients) to:
- Log **method entry**, **exit**, and **exceptions**.
- Inject **traceId** from OpenTelemetry into MDC for log correlation.
- Use **internationalized** log messages based on user locale.

---

### 📦 **Main Components**

#### 1. `CustomLoggingConfig.java`
- **Purpose**: Configures a custom `MessageSource` bean to load localized messages for logging.
- **Bean**: `customMessageSource` – Loads messages from `classpath:loggingMessages.properties`.

```java
messageSource.setBasename("classpath:loggingMessages");
messageSource.setDefaultEncoding("UTF-8");
```

---

#### 2. `LoggingAspect.java`
- **Purpose**: Implements **cross-cutting logging** using AOP for service/method calls and exception handling.
- **Logger**: Uses SLF4J `LoggerFactory`.
- **Trace ID**: Injects `traceId` from OpenTelemetry `Span` into MDC.
- **Locale Support**: Fetches messages from `customMessageSource` based on current locale.

---

### 🔄 **Advice Methods**

#### 🌀 `logAround(...)`
- **Pointcut**: All methods in the base package excluding:
  - `.configuration.*`
  - `.clients.*`
- **Logs**:
  - Method entry (with arguments)
  - Method exit
  - Exceptions with localized message and stack trace

#### 🌀 `logExceptionHandlerResponse(...)`
- **Pointcut**: Targets only `GlobalExceptionHandler` methods.
- **Logs**:
  - Entry into exception handler
  - Response returned from exception handler

---

### 📄 **Message Properties File** (`loggingMessages.properties`)
```properties
log.method.entry=Entering method {0}
log.method.exit=Exiting method {0}
log.method.error=Error in method {0}: {1}
log.method.runtime.error=Runtime exception in method {0}: {1}
```
- Supports **localization** of logs using parameterized messages.

---

### 🌐 **OpenTelemetry Integration**
- Uses `Span.current().getSpanContext().getTraceId()` to fetch current traceId.
- Injected into MDC so it can be included in all logs for **correlation** across distributed systems.

---

### 🛡️ **Dependencies**
Ensure these are present:
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

---

### 🚫 **Excluded from Logging**
- Classes in:
  - `com.sherwin.edps.batchmgmtbff.configuration`
  - `com.sherwin.edps.batchmgmtbff.clients`
- This is to avoid redundant or unnecessary logs from configuration and external client layers.

---

### ✅ **Benefits**
- Centralized and reusable logging logic
- Enhanced observability with traceId injection
- Locale-aware logs for better international support
- Cleaner business logic without repetitive logging code

---

Let me know if you want this in Markdown format for a README or as a PDF/docx.