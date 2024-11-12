public Mono<WorkOrder[]> getGroupNumberDetails(String organizationCode, String workOrderGroupNumber) {
    return Mono.deferContextual(c -> {
        long startTime = System.currentTimeMillis();

        return c.getOrEmpty("AzureToken")
                .map(azureToken -> client.get()
                        .uri(uriBuilder -> {
                            buildListWorkOrdersGroupNumberUri(organizationCode, workOrderGroupNumber, uriBuilder);
                            return uriBuilder.build();
                        })
                        .header("Authorization", c.get("AzureToken").toString())
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                            logger.error("Received an error status code: {}", response.statusCode());
                            return response.bodyToMono(ApiError.class)
                                    .flatMap(error -> Mono.error(new WorkOrderDomainException(error)));
                        })
                        .bodyToMono(new ParameterizedTypeReference<PagedResponse<WorkOrder>>() {})
                        .map(workOrdersResponse -> {
                            long endTime = System.currentTimeMillis();
                            logger.info("Time taken for first API call: {} ms", (endTime - startTime));
                            return Arrays.stream(workOrdersResponse.getItems()).toArray(WorkOrder[]::new);
                        })
                )
                .orElseGet(() -> {
                    long startTimeSecondCall = System.currentTimeMillis();

                    return client.mutate()
                            .baseUrl(workOrderInternalUrl)
                            .defaultHeader("X-API-KEY", workOrderDomainApiKey)
                            .build().get()
                            .uri(uriBuilder -> {
                                buildListWorkOrdersGroupNumberUri(organizationCode, workOrderGroupNumber, uriBuilder);
                                return uriBuilder.build();
                            })
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                                logger.error("Received an error status code for fallback: {}", response.statusCode());
                                return response.bodyToMono(ApiError.class)
                                        .flatMap(error -> Mono.error(new WorkOrderDomainException(error)));
                            })
                            .bodyToMono(new ParameterizedTypeReference<PagedResponse<WorkOrder>>() {})
                            .map(workOrdersResponse -> {
                                long endTimeSecondCall = System.currentTimeMillis();
                                logger.info("Time taken for second API call: {} ms", (endTimeSecondCall - startTimeSecondCall));
                                return Arrays.stream(workOrdersResponse.getItems()).toArray(WorkOrder[]::new);
                            });
                });
    });
}
