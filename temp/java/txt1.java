import java.time.Duration;
import java.time.Instant;

public Mono<WorkOrder> updateWorkOrder(WorkOrderUpdateRequest workOrderUpdateRequest) {
    Instant start = Instant.now();  // Start time

    return client.patch()
        .uri(uriBuilder -> {
            uriBuilder.path("/work-orders/");
            uriBuilder.path(workOrderUpdateRequest.getWorkOrderId().toString());
            return uriBuilder.build();
        })
        .bodyValue(workOrderUpdateRequest)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                  response -> response.bodyToMono(ApiError.class)
                      .flatMap(error -> Mono.error(new WorkOrderDomainException(error))))
        .bodyToMono(WorkOrder.class)
        .doOnSuccess(response -> {
            Instant end = Instant.now();  // End time
            System.out.println("API call elapsed time: " + Duration.between(start, end).toMillis() + " ms");
        });
}
