@Test
    void testPostResponse_TranIdIsBlank() {
        // Arrange
        String tranId = "";
        int idxScore = 100;

        // Act
        responseService.postResponse(crcList, idxScore, tranId);

        // Assert
        verify(tranScoresRepository, never()).saveAndFlush(any());
    }