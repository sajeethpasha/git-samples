import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class text3 {
    private static final Logger logger = LoggerFactory.getLogger(text3.class);

    public Mono<InventoryLotNumber> createLotNumber(String organizationCode, String workOrderNumber, Boolean partialLotFlag) {
        long startTime = System.nanoTime();  // Start time in nanoseconds

        return client.get()
            .uri(uriBuilder -> {
                uriBuilder.path("/code2Logic")
                    .queryParam("organizationCode", organizationCode)
                    .queryParam("workOrderNumber", workOrderNumber)
                    .queryParam("partialLotFlag", partialLotFlag);
                return uriBuilder.build();
            })
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(
                status -> status.is4xxClientError() || status.is5xxServerError(),
                response -> response.bodyToMono(String.class)
                    .flatMap(error -> Mono.error(new RuntimeException(error)))
            )
            .bodyToMono(new ParameterizedTypeReference<InventoryLotNumberResponse[]>() {})
            .map(response -> {
                if (Arrays.stream(response).count() > 0) {
                    return response[0].toModel();
                }
                throw new RuntimeException("No response returned from lot number service");
            })
            .onErrorMap(error -> {
                logger.error("Non-Group lot number creation failed with exception: {}", error.getMessage());
                return new LotNumberException(error.getMessage());
            })
            .doOnSuccess(response -> {
                long endTime = System.nanoTime();  // End time in nanoseconds
                long durationInMillis = (endTime - startTime) / 1_000_000;  // Convert nanoseconds to milliseconds
                logger.info("API call elapsed time: {} ms", durationInMillis);
            });
    }
}
