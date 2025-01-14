package com.sherwin.edps.batchmgmtbff.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to mark parameters or fields for sanitization.
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Sanitized {
    // You can add optional metadata if needed
}

----------------------------------------------------------------


package com.sherwin.edps.batchmgmtbff.common;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;

@Aspect
@Component
@Slf4j
public class SanitizationAspect {

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping)")
    public Object sanitizeParameters(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = methodSignature.getMethod().getParameters();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            // Check if the parameter is annotated with @Sanitized
            if (parameter.isAnnotationPresent(Sanitized.class)) {
                if (args[i] instanceof String) {
                    args[i] = sanitizeString((String) args[i]);
                    log.debug("Sanitized parameter: {}", args[i]);
                } else {
                    log.warn("Sanitization only supports String parameters. Skipping parameter: {}", parameter.getName());
                }
            }
        }

        return joinPoint.proceed(args);
    }

    private String sanitizeString(String input) {
        if (input == null) {
            return null;
        }
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;")
                .replace("(", "&#40;")
                .replace(")", "&#41;")
                .replace("/", "&#47;");
    }
}
