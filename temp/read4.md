@GetMapping("/rework-items/{organization-code}")
public ResponseEntity<Mono<List<InventoryOnhandDetails>>> getReworkItems(
    @PathVariable("organization-code") String organizationCode
) {
    // Sanitize the input for logging
    String sanitizedOrganizationCode = sanitizeForLog(organizationCode);

    log.info("Fetching rework items for sanitized organization code: {}", sanitizedOrganizationCode);

    return new ResponseEntity<>(
        itemService.getReworkItemsOnhandDetails(sanitizedOrganizationCode),
        HttpStatus.OK
    );
}

// Utility method to sanitize data for logging
private String sanitizeForLog(String input) {
    if (input == null) {
        return "null";
    }
    // Remove newline and other potentially harmful characters
    return input.replaceAll("[\\r\\n]", "_");
}fd
f

sudhakar

