import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class text2 {
    private static final Logger logger = LoggerFactory.getLogger(text2.class);

    public Mono<InventoryLotNumber> createLotNumber(String organizationCode, String workOrderNumber, Boolean partialLotFlag) {
        Instant start = Instant.now();  // Start time

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
                Instant end = Instant.now();  // End time
                logger.info("API call elapsed time: {} ms", Duration.between(start, end).toMillis());
            });
    }
}
