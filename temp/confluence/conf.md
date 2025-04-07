Below is a Confluence-style documentation page that explains the AOP-based logging implementation. You can copy this content into your Confluence page for management and technical review.

---

h1. AOP-Based Logging Implementation Documentation

h2. Overview

This documentation describes the implementation of an AOP (Aspect-Oriented Programming) based logging solution used within our application. The goal of this implementation is to provide detailed logging for method entries, exits, and exceptions, along with traceability features using OpenTelemetry. The logging functionality is externalized using a message source to allow for internationalization and ease of maintenance. This approach ensures that logging concerns remain separated from the business logic, leading to a cleaner and more maintainable codebase.

h2. Components

h3. 1. Custom Logging Configuration

*File: CustomLoggingConfig.java*

* **Purpose:**  
  Configures a Spring-managed bean for the logging message source.  
* **Key Details:**  
  - **MessageSource Bean:** A bean named "customMessageSource" is defined using `ReloadableResourceBundleMessageSource`.
  - **Externalized Logging Messages:** The base name is set to "classpath:loggingMessages" so that the logging messages can be managed outside the code.
  - **Encoding:** UTF-8 is configured as the default encoding to properly support international characters.
  
* **Benefits:**  
  - Enables the externalization and localization of log messages.  
  - Simplifies maintenance by decoupling log message definitions from code.

h3. 2. Logging Aspect

*File: LoggingAspect.java*

* **Purpose:**  
  Implements the AOP-based logging mechanism to intercept method calls for enhanced debugging and error logging.
  
* **Key Features:**

  - **Method Interception:**  
    The aspect intercepts all methods within the base package (`com.sherwin.edps.batchmgmtbff..*`) except for those in the configuration and client packages.
    
  - **Trace Correlation:**  
    Uses OpenTelemetry to extract the current span’s trace ID and adds it to the MDC (Mapped Diagnostic Context). This allows logs to be correlated across distributed systems.
    
  - **Localized Log Messages:**  
    Log messages are retrieved from the externalized message source using keys such as:
    - *log.method.entry*: "Entering method {0}"
    - *log.method.exit*: "Exiting method {0}"
    - *log.method.error*: "Error in method {0}: {1}"
    - *log.method.runtime.error*: "Runtime exception in method {0}: {1}"
    
  - **Method Execution Logging:**  
    - **Entry Logging:** Logs the method name and arguments before execution.
    - **Exit Logging:** Logs a message upon successful method completion.
    - **Error Logging:** Catches exceptions, logs the error message and full stack trace, and rethrows the exception to ensure proper error handling.
    
  - **Global Exception Handler Logging:**  
    A dedicated pointcut targets any methods in classes with names ending in `GlobalExceptionHandler`. It logs the invocation and response of the exception handler, ensuring comprehensive error reporting.
    
* **Benefits:**  
  - Improves the observability and debuggability of the application.
  - Enhances error tracking by correlating logs with trace IDs.
  - Provides a clear separation of logging concerns from business logic, aiding in maintenance and future enhancements.

h3. 3. Logging Messages

*File: loggingMessages.properties*

* **Purpose:**  
  Defines the templates for logging messages used across the application.
  
* **Key Properties:**
  - `log.method.entry=Entering method {0}`
  - `log.method.exit=Exiting method {0}`
  - `log.method.error=Error in method {0}: {1}`
  - `log.method.runtime.error=Runtime exception in method {0}: {1}`
  
* **Benefits:**  
  - Enables consistency in log messaging.
  - Facilitates internationalization by allowing messages to be localized as needed.

h3. 4. Maven Configuration

*File: pom.xml*

* **Purpose:**  
  Manages all project dependencies and build configurations required for running the application.
  
* **Key Dependencies:**
  - **Spring Boot Starters:** WebFlux, Actuator, AOP – provide the core runtime and AOP support.
  - **OpenTelemetry API:** Used for extracting the trace ID for log correlation.
  - **Security and Documentation:** Additional dependencies for security and API documentation.
  - **AspectJ Weaver:** Required for weaving aspects at runtime.
  - **Testing Libraries:** Includes dependencies such as JUnit, Mockito, and WireMock for comprehensive testing.
  
* **Plugins:**  
  - **Spring Boot Maven Plugin:** For packaging and running the application.
  - **Maven Surefire Plugin:** For executing unit tests.
  - **JaCoCo Maven Plugin:** For generating code coverage reports.

* **Benefits:**  
  - Ensures a robust and consistent build process.
  - Supports maintainability and scalability by centralizing configuration management.

h2. Benefits from a Management Perspective

* **Enhanced Debugging and Monitoring:**  
  Detailed logging of method invocations and errors provides visibility into the application’s behavior. The inclusion of trace IDs aids in troubleshooting across distributed systems.
  
* **Separation of Concerns:**  
  Using AOP for logging decouples the logging logic from business logic. This simplifies code maintenance and reduces the risk of errors when updating core functionalities.
  
* **Internationalization and Flexibility:**  
  Externalized log messages support localization and make it easier to adapt the log messages without modifying code.
  
* **Operational Efficiency:**  
  Comprehensive logging supports faster resolution of issues and improved operational oversight. This is crucial for maintaining high application uptime and performance.
  
* **Compliance and Audit:**  
  Detailed logs help meet audit requirements and compliance standards by providing clear and traceable records of application behavior.

h2. Conclusion

The AOP-based logging implementation provides a robust, scalable, and maintainable solution to track and debug application behavior. By externalizing log messages and integrating with OpenTelemetry for trace correlation, the system ensures that operational issues can be quickly identified and resolved. This documentation outlines the technical details and the associated benefits, ensuring that both management and development teams have a clear understanding of the solution.

---

*End of Documentation*