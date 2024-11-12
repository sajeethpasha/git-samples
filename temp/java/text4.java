public Mono<WorkOrderOutputLotNumber[]> listOperationOutputLotNumbers(Long id, Long operationId, Long outputOperationId) {
    return Mono.deferContextual(c -> {
        long startTime = System.currentTimeMillis();

        return c.getOrEmpty("AzureToken")
                .map(azureToken -> client.get()
                        .uri(uriBuilder -> {
                            uriBuilder.path("/" + id);
                            uriBuilder.path("/operations");
                            uriBuilder.path("/" + operationId);
                            uriBuilder.path("/outputs");
                            uriBuilder.path("/" + outputOperationId);
                            uriBuilder.path("/lot-numbers");
                            return uriBuilder.build();
                        })
                        .header("Authorization", c.get("AzureToken").toString())
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                            System.out.println("Received an error status code: " + response.statusCode());
                            return response.bodyToMono(ApiError.class)
                                    .flatMap(error -> Mono.error(new WorkOrderDomainException(error)));
                        })
                        .bodyToMono(new ParameterizedTypeReference<PagedResponse<WorkOrderOutputLotNumber>>() {})
                        .map(workOrdersResponse -> {
                            long endTime = System.currentTimeMillis();
                            System.out.println("Time taken for first API call: " + (endTime - startTime) + " ms");
                            return Arrays.stream(workOrdersResponse.getItems()).toArray(WorkOrderOutputLotNumber[]::new);
                        })
                )
                .orElseGet(() -> {
                    long startTimeSecondCall = System.currentTimeMillis();

                    return client.mutate()
                            .baseUrl(workOrderInternalUrl)
                            .defaultHeader("X-API-KEY", workOrderDomainApiKey)
                            .build().get()
                            .uri(uriBuilder -> {
                                uriBuilder.path("/" + id);
                                uriBuilder.path("/operations");
                                uriBuilder.path("/" + operationId);
                                uriBuilder.path("/outputs");
                                uriBuilder.path("/" + outputOperationId);
                                uriBuilder.path("/lot-numbers");
                                return uriBuilder.build();
                            })
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                                System.out.println("Received an error status code for fallback: " + response.statusCode());
                                return response.bodyToMono(ApiError.class)
                                        .flatMap(error -> Mono.error(new WorkOrderDomainException(error)));
                            })
                            .bodyToMono(new ParameterizedTypeReference<PagedResponse<WorkOrderOutputLotNumber>>() {})
                            .map(workOrdersResponse -> {
                                long endTimeSecondCall = System.currentTimeMillis();
                                System.out.println("Time taken for second API call: " + (endTimeSecondCall - startTimeSecondCall) + " ms");
                                return Arrays.stream(workOrdersResponse.getItems()).toArray(WorkOrderOutputLotNumber[]::new);
                            });
                });
    });
}
