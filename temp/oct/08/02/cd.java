package ca.tuc.idx.bizlogic.service.impl;

import org.slf4j.*;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostResponseServiceImpl implements PostResponseService {

    private final TranScoresRepository scoresRepository;
    private final CrcsRepository crcsRepository;

    // Static final map to store the CRC prefixes and their lengths
    private static final Map<String, Integer> CRC_LENGTH_MAP = new HashMap<>();

    static {
        CRC_LENGTH_MAP.put("G0401", 139);
        CRC_LENGTH_MAP.put("G0402", 252);
        CRC_LENGTH_MAP.put("G0403", 252);
    }

    /**
     * Prepare and persist the IDX Score and CRCs to the DB
     * @param crcList - IDX response with score and CRC values
     * @param tranId - transaction id for the IDX request
     */
    @Override
    @Transactional
    @LogExecutionTime
    public void postResponse(List<IdxCrc> crcList, int idxScore, String tranId) {
        if (StringUtils.isBlank(tranId)) {
            log.error("Missing transaction ID, cannot save CRCs..");
            return;
        }

        log.debug("Start saving score and CRCs for tranId=" + tranId);
        TranScores tranScores = new TranScores(tranId, idxScore);
        Optional.ofNullable(crcList)
            .orElse(new ArrayList<>())
            .forEach(c -> {
                String value = c.getValue();
                if (value != null) {
                    // Check if the CRC prefix exists in the map, if so, call parseMethod
                    for (Map.Entry<String, Integer> entry : CRC_LENGTH_MAP.entrySet()) {
                        if (value.startsWith(entry.getKey())) {
                            value = parseMethod(value, entry.getValue());
                            break;
                        }
                    }
                    ResultCrcs resultCrcs = new ResultCrcs(c.getName(), value);
                    tranScores.add(resultCrcs);
                }
            });

        try {
            log.debug("Saving IDX CRC and scores for ID= " + tranScores.getTransactionId());
            scoresRepository.saveAndFlush(tranScores);
        } catch (Exception e) {
            log.error("Exception saving CRC values", e);
        }
    }

    /**
     * Method to handle parsing of the CRC value.
     * @param value - The CRC value to be parsed
     * @param length - The length to trim to
     * @return - The parsed (trimmed) value
     */
    private String parseMethod(String value, int length) {
        if (value == null) {
            return null;
        }
        return value.length() > length ? value.substring(0, length) : value;
    }

    @Override
    @LogExecutionTime
    public void removeDuplicateScores(String tranId) {
        List<TranScores> tranScores = scoresRepository.findTranScoreByTransactionID(tranId);
        if (CollectionUtils.isNotEmpty(tranScores)) {
            log.info(String.format("Score already exists for tranId: %s, removing existing score and CRCs", tranId));
            deleteCrcs(tranScores);
            log.info("Update TRANS_SCORES started");
            scoresRepository.deleteTranScoresByTransactionID(tranId);
            log.info("Update TRANS_SCORES ended");
        }
    }

    private void deleteCrcs(List<TranScores> tranScores) {
        if (null != tranScores && !tranScores.isEmpty()) {
            for (TranScores t : tranScores) {
                log.info("Update CRCS started");
                crcsRepository.deleteCrcsBySerialNum(t.getSerialNum());
                log.info("Update CRCS ended");
            }
        }
    }
}
