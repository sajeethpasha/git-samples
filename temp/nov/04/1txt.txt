# Performance Improvements in Reactive Code

This document describes the performance improvements made to a reactive Java method for creating lot numbers using `WebClient`. Specifically, it explains why `flatMap` is used instead of `map` and how centralized error handling with `onErrorResume` is beneficial.

## 1. Why Use `flatMap` Instead of `map`

In reactive programming with Project Reactor, `map` and `flatMap` are used to transform data, but they have different behaviors when handling reactive types like `Mono` and `Flux`.

- **`map`**: This operator is used to transform data by applying a function to each element in the stream. It’s best used when the transformation is **synchronous** and when converting an item to another item type (e.g., `String` to `Integer`). However, `map` **does not** "flatten" reactive types. If it encounters another reactive type inside the transformation, it will wrap the result in a new `Mono<Mono<T>>` or `Mono<Flux<T>>`.

- **`flatMap`**: Similar to `map`, but it "flattens" the stream when the function returns a reactive type. This is useful when your transformation produces another reactive type (like `Mono` or `Flux`), allowing you to avoid nested reactive types (like `Mono<Mono<T>>`). `flatMap` is **asynchronous**, making it ideal for non-blocking calls that return a reactive type.

### Example in the Code

In the original code, `map` was used to transform the response array to a `Mono<InventoryLotNumber>`. Since `response[0].toModel()` returns a `Mono<InventoryLotNumber>`, using `map` would result in a nested structure like `Mono<Mono<InventoryLotNumber>>`. Using `flatMap` instead of `map` "flattens" the response, giving a single `Mono<InventoryLotNumber>` directly.

Here’s how the transformation with `flatMap` works:

```java
.flatMap(response -> {
    if (response.length > 0) {
        return Mono.just(response[0].toModel()); // This returns a Mono<InventoryLotNumber>
    }
    return Mono.error(new RuntimeException("No response returned from lot number service"));
})
```

- If `response.length > 0`, we return `Mono.just(response[0].toModel())`, which is a `Mono<InventoryLotNumber>`.
- If the array is empty, we return a `Mono.error`, which represents an error in the reactive stream.

Using `flatMap` ensures that the overall result is a `Mono<InventoryLotNumber>` instead of `Mono<Mono<InventoryLotNumber>>`, which would require an additional step to access the data.

## 2. Centralized Error Handling with `onErrorResume`

`onErrorResume` is a reactive operator used to handle errors gracefully. Instead of letting an error propagate and terminate the stream, `onErrorResume` allows you to define alternative behavior (e.g., logging the error or providing a fallback value).

### Explanation of `onErrorResume`

Here’s how `onErrorResume` is applied in the code:

```java
.onErrorResume(error -> {
    logger.error("Non-Group lot number creation failed with exception: {}", error.getMessage());
    return Mono.error(new LotNumberException(error.getMessage()));
});
```

- **Error Logging**: The error is logged using `logger.error(...)`. This makes it easy to debug and find the root cause of issues when something goes wrong.
- **Custom Exception Handling**: Instead of propagating a generic error, it is wrapped in a `LotNumberException` with a custom message. This way, the rest of the application or any callers of this method receive a specific exception type (`LotNumberException`) with a meaningful message, making it easier to handle upstream.

Using `onErrorResume` ensures that any errors in the reactive chain are handled in one place, providing a consistent error response without having to add error handling at multiple points in the code.

## Full Explanation with Code

Below is the refactored method with `flatMap` and `onErrorResume` explained in context:

```java
public Mono<InventoryLotNumber> createLotNumber(String organizationCode, String workOrderNumber, Boolean partialLotFlag) {
    return client.get()
        .uri(uriBuilder -> uriBuilder.path("/codeZLogic")
            .queryParam("organizationCode", organizationCode)
            .queryParam("workOrderNumber", workOrderNumber)
            .queryParam("partialLotFlag", partialLotFlag)
            .build())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
            response -> response.bodyToMono(String.class)
                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
        .bodyToMono(new ParameterizedTypeReference<InventoryLotNumberResponse[]>() {})
        .flatMap(response -> {
            if (response.length > 0) {
                return Mono.just(response[0].toModel()); // Use flatMap to flatten Mono<InventoryLotNumber>
            }
            return Mono.error(new RuntimeException("No response returned from lot number service"));
        })
        .onErrorResume(error -> {
            logger.error("Non-Group lot number creation failed with exception: {}", error.getMessage());
            return Mono.error(new LotNumberException(error.getMessage())); // Centralized error handling
        });
}
```

### Key Takeaways

1. **`flatMap` for Asynchronous Transformations**:
   - `flatMap` is used instead of `map` to avoid wrapping the response in nested `Mono` objects, allowing direct access to the transformed data. It’s ideal for cases where the transformation function returns a reactive type (`Mono` or `Flux`).

2. **Centralized Error Handling with `onErrorResume`**:
   - `onErrorResume` handles any errors that occur in the stream. It logs the error and wraps it in a custom exception (`LotNumberException`). This approach provides a consistent and clear way to handle errors, making the code cleaner and more maintainable.

These changes streamline the reactive pipeline, making it more performant, readable, and easier to debug.

