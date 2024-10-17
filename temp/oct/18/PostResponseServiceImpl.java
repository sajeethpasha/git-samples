package ca.tuc.idx.bizlogic.service.impl;

import ca.tuc.commons.clients.service.idx.model.request.IdxCrc;
import ca.tuc.idx.bizlogic.model.idx.ResultCrcs;
import ca.tuc.idx.bizlogic.model.idx.TranScores;
import ca.tuc.idx.bizlogic.repository.idx.CrcsRepository;
import ca.tuc.idx.bizlogic.repository.idx.TranScoresRepository;
import ca.tuc.idx.bizlogic.service.PostResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostResponseServiceImpl implements PostResponseService {

    private final TranScoresRepository scoresRepository;
    private final CrcsRepository crcsRepository;

    // Static final map to store the CRC prefixes and their start and end positions
    private static final Map<String, Pair<Integer, Integer>> CRC_POSITION_MAP;

    static {
        Map<String, Pair<Integer, Integer>> map = new HashMap<>();
        map.put("GO401", Pair.of(0, 139));
        map.put("DS03", Pair.of(0, 139));
        map.put("GO252", Pair.of(50, 60));
        map.put("GO403", Pair.of(50, 60));
        map.put("DS04", Pair.of(50, 60));
        map.put("GO402", Pair.of(0, 139)); // Added based on crcList
        // Add other CRC mappings as needed
        CRC_POSITION_MAP = Collections.unmodifiableMap(map);
    }

    @Override
    @Transactional
    @LogExecutionTime
    public void postResponse(List<IdxCrc> crcList, int idxScore, String tranId) {
        if (StringUtils.isBlank(tranId)) {
            log.error("Missing transaction ID, cannot save CRCs.");
            return;
        }

        log.debug("Start saving score and CRCs for tranId={}", tranId);
        TranScores tranScores = new TranScores(tranId, idxScore);

        Optional.ofNullable(crcList).orElse(Collections.emptyList()).forEach(c -> {
            String trimmedValue = processCrc(c.getName(), c.getValue());

            if (trimmedValue != null) {
                if ("DS04".equals(c.getName())) {
                    int[][] fieldsToRetain = {
                        {1, 4},   // Segment Type (DS04)
                        {5, 3},   // Segment Length
                        {8, 1},   // Source Match
                        {9, 8},   // Source Date
                        {125, 1}, // Last Name Match Flag
                        {126, 1}, // First Name Match Flag
                        {127, 1}, // Middle Name Match Flag
                        {128, 1}, // Street/Civic Number Match Flag
                        {129, 1}, // Street Name Match Flag
                        {130, 1}, // Apartment Number Match Flag
                        {131, 1}, // City Name Match Flag
                        {132, 1}, // Province Match Flag
                        {133, 1}, // Postal Code Match Flag
                        {134, 1}, // Telephone Number Match Flag
                        {135, 1}, // Date of Birth Match Flag
                        {136, 1}, // SIN Match Flag
                    };

                    String modifiedPayload = modifyPayload(trimmedValue, fieldsToRetain);
                    ResultCrcs resultCrcs = new ResultCrcs(c.getName(), modifiedPayload);
                    tranScores.addCrc(resultCrcs);
                } else {
                    ResultCrcs resultCrcs = new ResultCrcs(c.getName(), trimmedValue);
                    tranScores.addCrc(resultCrcs);
                }
            }
        });

        try {
            log.debug("Saving IDX CRC and scores for ID={}", tranScores.getTransactionId());
            scoresRepository.saveAndFlush(tranScores);
        } catch (Exception e) {
            log.error("Exception saving CRC values", e);
        }
    }

    /**
     * Modifies the given payload by retaining only the specified fields and replacing the rest with spaces.
     *
     * @param payload        The original payload string.
     * @param fieldsToRetain A 2D array where each element is an array containing the start position and length of a field to retain.
     * @return The modified payload with only the specified fields retained.
     */
    public static String modifyPayload(String payload, int[][] fieldsToRetain) {
        if (StringUtils.isBlank(payload)) {
            return payload;
        }

        // Initialize a StringBuilder with spaces
        StringBuilder modifiedPayload = new StringBuilder(StringUtils.repeat(' ', payload.length()));

        for (int[] field : fieldsToRetain) {
            int start = field[0] - 1; // Convert to 0-based index
            int length = field[1];
            int end = start + length;

            if (start >= 0 && end <= payload.length()) {
                String valueToRetain = payload.substring(start, end);
                modifiedPayload.replace(start, end, valueToRetain);
            } else {
                log.warn("Field positions out of bounds. Payload length: {}, Start: {}, End: {}", 
                         payload.length(), start, end);
            }
        }

        return modifiedPayload.toString();
    }

    /**
     * Processes a CRC value based on its name by extracting the substring defined in CRC_POSITION_MAP.
     *
     * @param name  The name of the CRC.
     * @param value The CRC value to be processed.
     * @return The extracted substring or the original value if no mapping is found.
     */
    private String processCrc(String name, String value) {
        Pair<Integer, Integer> positions = CRC_POSITION_MAP.get(name);
        if (positions != null) {
            return parseMethod(value, positions.getLeft(), positions.getRight());
        } else {
            log.warn("No parsing positions found for CRC name: {}", name);
        }
        return value; // Depending on requirements, you might choose to return null instead
    }

    /**
     * Parses the CRC value based on start and end indices.
     *
     * @param value      The CRC value to parse.
     * @param beginIndex The start index (inclusive, 0-based).
     * @param endIndex   The end index (exclusive).
     * @return The parsed substring or null if value is null or indices are invalid.
     */
    private String parseMethod(String value, int beginIndex, int endIndex) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        if (beginIndex < 0 || endIndex > value.length() || beginIndex >= endIndex) {
            log.warn("Invalid indices for parsing CRC value. Value length: {}, beginIndex: {}, endIndex: {}", 
                     value.length(), beginIndex, endIndex);
            return null;
        }
        return value.substring(beginIndex, endIndex);
    }

    @Override
    @LogExecutionTime
    public void removeDuplicateScores(String tranId) {
        List<TranScores> tranScores = scoresRepository.findTranScoreByTransactionID(tranId);

        if (CollectionUtils.isNotEmpty(tranScores)) {
            log.info("Score already exists for tranId: {}, removing existing score and CRCs", tranId);
            deleteCrcs(tranScores);
            log.info("Update TRANS_SCORES started");
            scoresRepository.deleteTranScoresByTransactionID(tranId);
            log.info("Update TRANS_SCORES ended");
        }
    }

    private void deleteCrcs(List<TranScores> tranScores) {
        if (CollectionUtils.isNotEmpty(tranScores)) {
            for (TranScores t : tranScores) {
                log.info("Update CRCS started for SerialNum={}", t.getSerialNum());
                crcsRepository.deleteCrcsBySerialNum(t.getSerialNum());
                log.info("Update CRCS ended for SerialNum={}", t.getSerialNum());
            }
        }
    }
}
