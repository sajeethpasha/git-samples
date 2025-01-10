package com.example.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Set;

@Aspect
@Component
@Slf4j
public class ParameterSanitizationAspect {

    // Cache for reflection-accessed fields to reduce redundant lookups
    private final Set<String> sanitizedFieldsCache = new HashSet<>();

    @Around("execution(* com.example.demo.controller..*(..))") // Intercept all controller methods
    public Object sanitizeApiInputs(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = signature.getMethod().getParameters();
        Object[] args = joinPoint.getArgs();

        log.info("Sanitizing parameters for method: {}", signature.getMethod().getName());

        // Iterate through method parameters
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            // Handle @RequestParam and @PathVariable
            if (parameter.isAnnotationPresent(RequestParam.class) || parameter.isAnnotationPresent(PathVariable.class)) {
                args[i] = sanitizeSimpleArgument(args[i]);
                log.debug("Sanitized {}: {}", parameter.getName(), args[i]);
            } 
            // Handle @RequestBody
            else if (parameter.isAnnotationPresent(RequestBody.class)) {
                args[i] = sanitizeRequestBody(args[i]);
                log.debug("Sanitized @RequestBody: {}", args[i]);
            }
        }

        // Proceed with sanitized arguments
        return joinPoint.proceed(args);
    }

    // Sanitize simple input arguments (String, PathVariable, RequestParam)
    private Object sanitizeSimpleArgument(Object arg) {
        if (arg instanceof String) {
            return ((String) arg).trim().replaceAll("[^a-zA-Z0-9 _-]", ""); // Allow alphanumeric, spaces, underscores, and dashes
        } else if (arg instanceof Integer || arg instanceof Long) {
            return validateNumericValue((Number) arg); // Validate Integer or Long
        }
        return arg; // Return other types as-is
    }

    // Validate and sanitize Integer and Long values
    private Number validateNumericValue(Number value) {
        if (value == null) {
            throw new IllegalArgumentException("Numeric value cannot be null.");
        }
        if (value.longValue() < 0) {
            throw new IllegalArgumentException("Numeric value cannot be negative: " + value);
        }
        return value; // Return valid values as-is
    }

    // Sanitize @RequestBody objects (handle nested fields)
    private Object sanitizeRequestBody(Object requestBody) {
        if (requestBody == null) {
            return null;
        }

        // Use reflection to sanitize all String and numeric fields in the object
        Class<?> clazz = requestBody.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            String fieldKey = clazz.getName() + "." + field.getName();

            try {
                field.setAccessible(true);

                if (field.getType() == String.class && sanitizedFieldsCache.add(fieldKey)) {
                    String value = (String) field.get(requestBody);
                    if (value != null) {
                        field.set(requestBody, sanitizeSimpleArgument(value));
                    }
                } else if ((field.getType() == Integer.class || field.getType() == Long.class) && sanitizedFieldsCache.add(fieldKey)) {
                    Number value = (Number) field.get(requestBody);
                    field.set(requestBody, validateNumericValue(value));
                }

            } catch (IllegalAccessException e) {
                log.error("Failed to sanitize field: {}", field.getName(), e);
            }
        }
        return requestBody;
    }
}
