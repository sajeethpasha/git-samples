package ca.tuc.idx.bizlogic.service.impl;

import ca.tuc.commons.clients.service.idx.model.request.IdxCrc;
import ca.tuc.idx.bizlogic.model.idx.ResultCrcs;
import ca.tuc.idx.bizlogic.model.idx.TranScores;
import ca.tuc.idx.bizlogic.repository.idx.TranScoresRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostResponseServiceImplTest {

    @Mock
    private TranScoresRepository tranScoresRepository;

    @InjectMocks
    private PostResponseServiceImpl responseService;

    private String tranId;
    private List<IdxCrc> crcList;

    @BeforeEach
    public void setUp() {
        crcList = List.of(
                new IdxCrc("IDX_BE12", "1"),
                new IdxCrc("IDX_BE14", "0"),
                new IdxCrc("IDX_BE15", "99999"),
                new IdxCrc("IDX_BE23", "4"),
                new IdxCrc("IDX_BE24", "5"),
                new IdxCrc("IDX_ID22", "8")
        );
        tranId = "test-tranid";
    }

    @Test
    void postResponse() {
        responseService.postResponse(crcList, 32, tranId);
        verify(tranScoresRepository, atMost(1)).saveAndFlush(any());
    }

    @Test
    void testPostResponse_ValueIsNull() {
        // Arrange
        List<IdxCrc> crcListWithNull = Collections.singletonList(new IdxCrc("CRC1", null));
        String tranId = "TXN123";
        int idxScore = 100;

        // Act
        responseService.postResponse(crcListWithNull, idxScore, tranId);

        // Assert
        ArgumentCaptor<TranScores> captor = ArgumentCaptor.forClass(TranScores.class);
        verify(tranScoresRepository, atMost(1)).saveAndFlush(captor.capture());
        TranScores savedTranScores = captor.getValue();
        assertThat(savedTranScores.getCrcsList()).isNull();
    }

    @Test
    void testPostResponse_ValueStartsWithDS04() {
        // Arrange
        String idxCrcValue = "DS04252Y20201029CAPITAL CITY SAVINGS 023975015872036502 20240423MAC DONALD AFRICA YYYYYYYYYYYY";
        List<IdxCrc> crcListWithDS04 = Collections.singletonList(new IdxCrc("CRC_DS04", idxCrcValue));
        String tranId = "TXN456";
        int idxScore = 150;

        // Act
        responseService.postResponse(crcListWithDS04, idxScore, tranId);

        // Assert
        ArgumentCaptor<TranScores> captor = ArgumentCaptor.forClass(TranScores.class);
        verify(tranScoresRepository, atMost(1)).saveAndFlush(captor.capture());
        TranScores savedTranScores = captor.getValue();
        ResultCrcs result = savedTranScores.getCrcsList().get(0);
        assertThat(result.getCrcValue()).isNotNull();
        assertThat(result.getCrcValue()).isEqualTo(idxCrcValue);
    }

    @Test
    void testPostResponse_ValueDoesNotStartWithDS04() {
        // Arrange
        String idxCrcValue = "ABCD123456789";
        List<IdxCrc> crcListWithoutDS04 = Collections.singletonList(new IdxCrc("CRC_ABCD", idxCrcValue));
        String tranId = "TXN789";
        int idxScore = 200;

        // Act
        responseService.postResponse(crcListWithoutDS04, idxScore, tranId);

        // Assert
        ArgumentCaptor<TranScores> captor = ArgumentCaptor.forClass(TranScores.class);
        verify(tranScoresRepository, atMost(1)).saveAndFlush(captor.capture());
        TranScores savedTranScores = captor.getValue();
        assertThat(savedTranScores.getCrcsList()).isEmpty();
    }

    @Test
    void testPostResponse_ModifiedPayload() {
        // Arrange
        String idxCrcValue = "DS04252Y20201029CAPITAL CITY SAVINGS 023975015872036502 20240423MAC DONALD AFRICA YYYYYYYYYYYY";
        List<IdxCrc> crcListWithDS04 = Collections.singletonList(new IdxCrc("CRC_DS04", idxCrcValue));
        String tranId = "TXN999";
        int idxScore = 300;

        // Act
        responseService.postResponse(crcListWithDS04, idxScore, tranId);

        // Assert
        ArgumentCaptor<TranScores> captor = ArgumentCaptor.forClass(TranScores.class);
        verify(tranScoresRepository, atMost(1)).saveAndFlush(captor.capture());
        TranScores savedTranScores = captor.getValue();
        ResultCrcs result = savedTranScores.getCrcsList().get(0);
        assertThat(result.getCrcValue()).isNotNull();
        assertThat(result.getCrcValue()).contains("DS04");
    }

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

    @Test
    void testPostResponse_TranIdIsNull() {
        // Arrange
        String tranId = null;
        int idxScore = 100;

        // Act
        responseService.postResponse(crcList, idxScore, tranId);

        // Assert
        verify(tranScoresRepository, never()).saveAndFlush(any());
    }

    @Test
    void testPostResponse_ExceptionWhileSaving() {
        // Arrange
        String tranId = "TXN123";
        doThrow(new RuntimeException("Database error")).when(tranScoresRepository).saveAndFlush(any());

        // Act
        responseService.postResponse(crcList, 100, tranId);

        // Assert
        verify(tranScoresRepository, atMost(1)).saveAndFlush(any());
    }

    @Test
    void testRemoveDuplicateScores_Success() {
        // Arrange
        List<TranScores> tranScores = List.of(new TranScores());
        when(tranScoresRepository.findTranScoreByTransactionID(tranId)).thenReturn(tranScores);

        // Act
        responseService.removeDuplicateScores(tranId);

        // Assert
        verify(tranScoresRepository).deleteTranScoresByTransactionID(tranId);
    }

    @Test
    void testRemoveDuplicateScores_NoDuplicates() {
        // Arrange
        when(tranScoresRepository.findTranScoreByTransactionID(tranId)).thenReturn(Collections.emptyList());

        // Act
        responseService.removeDuplicateScores(tranId);

        // Assert
        verify(tranScoresRepository, never()).deleteTranScoresByTransactionID(tranId);
    }

    @Test
    void testDeleteCrcs_Success() {
        // Arrange
        List<TranScores> tranScores = List.of(new TranScores());

        // Act
        responseService.removeDuplicateScores(tranId);

        // Assert
        verify(tranScoresRepository).deleteTranScoresByTransactionID(tranId);
    }
}
