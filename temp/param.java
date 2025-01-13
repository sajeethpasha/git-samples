package com.example.demo.interceptor;

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
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

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





    private Object sanitizeRequestBody(Object requestBody) {
        if (requestBody == null) {
            return null; // Return immediately if requestBody is null
        }

        Class<?> clazz = requestBody.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                // Skip static or final fields
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }

                Object fieldValue = field.get(requestBody);
                if (fieldValue == null) {
                    continue; // Skip null fields
                }

                if (field.getType() == String.class) {
                    field.set(requestBody, sanitizeSimpleArgument((String) fieldValue));
                } else if (field.getType() == Integer.class || field.getType() == Long.class) {
                    field.set(requestBody, validateNumericValue((Number) fieldValue));
                } else if (!field.getType().isPrimitive() && !field.getType().getName().startsWith("java.")) {
                    sanitizeRequestBody(fieldValue); // Recursive sanitization for nested objects
                } else if (field.getType() == List.class || field.getType() == Set.class) {
                    sanitizeCollection((Collection<?>) fieldValue);
                } else if (field.getType() == Map.class) {
                    sanitizeMap((Map<?, ?>) fieldValue);
                } else {
                    log.debug("Skipping unsupported field type: {}", field.getType().getName());
                }

            } catch (IllegalAccessException e) {
                log.error("Failed to sanitize field: {}", field.getName(), e);
            } catch (NullPointerException e) {
                log.error("NullPointerException on field: {}", field.getName(), e);
            }
        }
        return requestBody;
    }

    private void sanitizeCollection(Collection<?> collection) {
        if (collection == null) return;
        collection.removeIf(Objects::isNull); // Remove null elements
        for (Object element : collection) {
            if (element != null && !element.getClass().isPrimitive() && !element.getClass().getName().startsWith("java.")) {
                sanitizeRequestBody(element); // Recursively sanitize nested objects
            }
        }
    }

    private void sanitizeMap(Map<?, ?> map) {
        if (map == null) return;
        map.entrySet().removeIf(entry -> entry.getKey() == null || entry.getValue() == null); // Remove null keys/values
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value != null && !value.getClass().isPrimitive() && !value.getClass().getName().startsWith("java.")) {
                sanitizeRequestBody(value); // Recursively sanitize nested objects
            }
        }
    }

}
