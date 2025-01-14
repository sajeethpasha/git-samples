@Around("execution(* com.sherwin.edps.batchmgmtbff.controllers..*(..))")
public Object sanitizeApiInputs(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Parameter[] parameters = signature.getMethod().getParameters();
    Object[] args = joinPoint.getArgs();

    log.info("Sanitizing parameters for method: {}", signature.getMethod().getName());

    for (int i = 0; i < parameters.length; i++) {
        Parameter parameter = parameters[i];

        if (parameter.getName().equals("uomCode")) {
            // Apply specific validation logic for uomCode
            args[i] = sanitizeUomCode((String) args[i]);
            log.debug("Sanitized uomCode: {}", args[i]);
        } else if (parameter.isAnnotationPresent(RequestParam.class) || parameter.isAnnotationPresent(PathVariable.class)) {
            args[i] = sanitizeSimpleArgument(args[i]);
            log.debug("Sanitized {}: {}", parameter.getName(), args[i]);
        } else if (parameter.isAnnotationPresent(RequestBody.class)) {
            args[i] = sanitizeRequestBody(args[i]);
            log.debug("Sanitized @RequestBody: {}", args[i]);
        }
    }

    return joinPoint.proceed(args);
}

private String sanitizeUomCode(String uomCode) {
    if (uomCode == null || uomCode.trim().isEmpty()) {
        throw new IllegalArgumentException("uomCode cannot be null or empty.");
    }
    // Additional validation: only allow alphanumeric characters and hyphens
    if (!uomCode.matches("^[a-zA-Z0-9-]+$")) {
        throw new IllegalArgumentException("Invalid uomCode: " + uomCode);
    }
    return uomCode.trim();
}
