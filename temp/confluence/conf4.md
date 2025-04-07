Below is an updated Confluence documentation section that includes code snippets along with explanations. These examples help illustrate how the AOP-based logging mechanism works.

---

h3. Code Snippets with Explanations

Below are two key code snippets with inline explanations:

----

h4. Custom Logging Configuration

*File: CustomLoggingConfig.java*

{code:java}
package com.sherwin.edps.batchmgmtbff.configuration;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class CustomLoggingConfig {

    @Bean(name = "customMessageSource")
    public MessageSource customMessageSource() {
        // Create a new ReloadableResourceBundleMessageSource instance
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        // Set the base name for the properties file containing log messages
        messageSource.setBasename("classpath:loggingMessages");
        // Define UTF-8 as the default encoding for proper character handling
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
{code}

*Explanation:*

- **ReloadableResourceBundleMessageSource:**  
  This class allows you to load externalized messages from a properties file. In our case, it looks for the file `loggingMessages.properties` in the classpath.
  
- **UTF-8 Encoding:**  
  Ensures that any international characters in the logging messages are displayed correctly.

- **Bean Declaration:**  
  The bean is named "customMessageSource", making it accessible to other parts of the application (like the logging aspect).

----

h4. Logging Aspect - Method Logging

*File: LoggingAspect.java (Excerpt)*

{code:java}
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private final MessageSource messageSource;

    @Autowired
    public LoggingAspect(@Lazy @Qualifier("customMessageSource") MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // Intercepts methods in specified packages excluding configuration and client packages
    @Around("within(com.sherwin.edps.batchmgmtbff..*) && !within(com.sherwin.edps.batchmgmtbff.configuration..*) && !within(com.sherwin.edps.batchmgmtbff.clients..*)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // Retrieve the current OpenTelemetry span and extract the trace ID
        Span currentSpan = Span.current();
        String traceId = currentSpan.getSpanContext().getTraceId();
        MDC.put("traceId", traceId);

        // Capture current locale and method signature for logging
        Locale locale = LocaleContextHolder.getLocale();
        String methodName = joinPoint.getSignature().toShortString();

        // Convert method arguments to a readable string format
        String args = Arrays.toString(joinPoint.getArgs());

        try {
            // Log method entry with a localized message
            String entryMessage = messageSource.getMessage("log.method.entry", new Object[]{methodName}, locale);
            logger.info("{} with arguments: {}", entryMessage, args);

            // Proceed with the actual method execution
            Object result = joinPoint.proceed();

            // Log method exit with a localized message
            String exitMessage = messageSource.getMessage("log.method.exit", new Object[]{methodName}, locale);
            logger.info(exitMessage);
            return result;
        } catch (Exception ex) {
            // Log error details using a localized error message
            String errorMessage = messageSource.getMessage("log.method.error", new Object[]{methodName, ex.getMessage()}, locale);
            logger.error(errorMessage, ex);
            // Optionally log the stack trace for debugging
            logger.error("Exception stack trace:", ex);
            // Rethrow the exception to ensure normal error handling by the caller
            throw ex;
        } finally {
            // Clear the MDC to avoid leaking the trace ID to other threads
            MDC.clear();
        }
    }
}
{code}

*Explanation:*

- **Aspect and Component Annotations:**  
  The `@Aspect` annotation designates this class as an AOP aspect, and `@Component` ensures it is managed by Spring.

- **MDC (Mapped Diagnostic Context):**  
  The code sets the trace ID from the current OpenTelemetry span into the MDC. This makes it possible to correlate log messages across different parts of the system.

- **Localized Logging:**  
  The `MessageSource` is used to fetch localized log messages from external properties. For example:
  - `log.method.entry` displays a message when a method is entered.
  - `log.method.exit` displays a message when a method exits.
  - `log.method.error` logs errors with the method name and error details.

- **Exception Handling:**  
  The `try-catch-finally` block ensures that all exceptions are logged properly, and the MDC is cleared after execution to maintain clean logging context.

----

These code snippets and their explanations are designed to give both technical and non-technical stakeholders an insight into how the logging mechanism works and the benefits it brings to the overall system. This not only helps in debugging and monitoring but also provides a clear separation of concerns, making the codebase easier to maintain.

---