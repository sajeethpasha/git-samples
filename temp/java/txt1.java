public Mono<InventoryLotNumber> createLotNumber(String organizationCode, String workOrderNumber, Boolean partialLotFlag) {
    logger.info("Create lot number with organization code: {} and work order number: {}", organizationCode, workOrderNumber);

    return webClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/codeZLogic")
                    .queryParam("organizationCode", organizationCode)
                    .queryParam("workOrderNumber", workOrderNumber)
                    .queryParam("partialLotFlag", partialLotFlag)
                    .build())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> 
                response.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            logger.error("Error response from lot number service: {}", errorBody);
                            return Mono.error(new RuntimeException(errorBody));
                        })
            )
            .bodyToMono(InventoryLotNumberResponse[].class)
            .flatMap(responseArray -> {
                if (responseArray.length > 0) {
                    return Mono.just(responseArray[0].toModel());
                } else {
                    return Mono.error(new RuntimeException("No response returned from lot number service"));
                }
            })
            .onErrorMap(error -> {
                logger.error("Non-Group lot number creation failed with exception: {}", error.getMessage());
                return new LotNumberException(error.getMessage());
            });
}
