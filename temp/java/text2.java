public Mono<List<InventoryLotNumber>> createLotNumbersForGroup(String organizationCode, String masterRexWorkOrderNumber, Boolean partialLotFlag) {
    logger.info("Creating lot numbers for group with organization code: {} and master work order number: {}", organizationCode, masterRexWorkOrderNumber);
    
    long startTime = System.currentTimeMillis();

    return client.get()
            .uri(uriBuilder -> {
                logger.info("Building URI for batch work order API call...");
                return uriBuilder
                        .path("/batchWorkOrders")
                        .queryParam("organizationCode", organizationCode)
                        .queryParam("workOrderGroupNumberSearch", masterRexWorkOrderNumber)
                        .queryParam("partialLotFlag", partialLotFlag)
                        .build();
            })
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                logger.error("Received an error status code: {}", response.statusCode());
                return response.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            logger.error("Error response from lot number service: {}", errorBody);
                            return Mono.error(new RuntimeException(errorBody));
                        });
            })
            .bodyToMono(InventoryLotNumberResponse[].class)
            .flatMap(responseArray -> {
                long endTime = System.currentTimeMillis();
                logger.info("Time taken for API call: {} ms", (endTime - startTime));
                
                if (responseArray.length > 0) {
                    return Mono.just(Arrays.stream(responseArray)
                                           .map(InventoryLotNumberResponse::toModel)
                                           .collect(Collectors.toList()));
                } else {
                    logger.error("No response returned from lot number service");
                    return Mono.error(new RuntimeException("No response returned from lot number service"));
                }
            })
            .doOnError(error -> {
                long endTime = System.currentTimeMillis();
                logger.error("Error occurred after {} ms: {}", (endTime - startTime), error.getMessage());
            })
            .onErrorMap(error -> {
                logger.error("Group lot number creation failed with exception: {}", error.getMessage());
                return new LotNumberException(error.getMessage());
            });
}
