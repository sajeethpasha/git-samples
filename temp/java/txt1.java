public Mono<List<LotNumber>> generateLotForGroupedBatch(String organizationCode, Boolean partialLotFlag, List<WorkOrder> sortedList) {
    System.out.println("Checking site code details for grouped batch for the organization code " + organizationCode);

    long startTime = System.currentTimeMillis();
    SiteCodeDetailsEntity siteDetails = siteCodesRepository.findByMeaningAndLookUpTypeEquals(organizationCode, "XXSW_CODEZ_ORG");
    long endTime = System.currentTimeMillis();

    System.out.println("Time taken to fetch site code details: " + (endTime - startTime) + " ms");

    if (siteDetails == null) {
        throw new LotNumberException("The Lot Number Cannot be generated for this Organization Code " + organizationCode);
    } else {
        System.out.println("sortedList size ===> " + sortedList.size());
        return Flux.fromIterable(sortedList)
                .concatMap(workOrders -> {
                    System.out.println("Checking item type for the item number in grouped batch " + workOrders.getItemNumber());
                    return itemService.checkItemType(partialLotFlag, workOrders);
                })
                .collectList()
                .flatMap(lotNumbers -> {
                    if (lotNumbers.isEmpty()) {
                        return Mono.error(new LotNumberException("No lot number is generated for grouped batch. Check logs for any errors"));
                    }
                    return Mono.just(lotNumbers);
                })
                .doOnError(e -> System.out.println("Error processing generateLotForNonGroupedBatch: " + e.getMessage()));
    }
}
