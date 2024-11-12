public Flux<LotNumber> checkItemType(Boolean partialLotFlag, WorkOrder workOrderDetails) {
    System.out.println("Calling list items to check item type for item number " + workOrderDetails.getItemNumber() + " in org code " + workOrderDetails.getOrganizationCode());

    long startTime = System.currentTimeMillis();

    return client.listItems(null, workOrderDetails.getItemNumber(), null, null, workOrderDetails.getOrganizationCode(), null)
            .flatMapMany(pagedResponse -> {
                long endTime = System.currentTimeMillis();
                System.out.println("Time taken to call list items API: " + (endTime - startTime) + " ms");

                if (pagedResponse.getItems().length == 0) {
                    return Mono.error(new LotNumberException("No items found for item number " + workOrderDetails.getItemNumber()));
                } else {
                    return Flux.fromArray(pagedResponse.getItems());
                }
            })
            .filter(item -> item.getItemNumber().equals(workOrderDetails.getItemNumber()))
            .concatMap(item -> validateAndProcessLotNumbers(partialLotFlag, workOrderDetails, item))
            .onErrorResume(Exception.class, e -> {
                System.out.println("Error while processing checkItemType: " + e.getMessage());
                return Mono.error(e);
            });
}
