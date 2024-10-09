/**
 * Utility class for modifying a payload by retaining specific fields and replacing the rest with spaces.
 */
public class PayloadModifier {

    /**
     * Main method to demonstrate the payload modification.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Original payload
        String payload = "DS04252Y20201029CAPITAL CITY SAVINGS 023975015872036502 20240423MAC DONALD AFRICA YYYYYYYYYYYY";

        // List of fields to retain, the rest will be emptied
        int[][] fieldsToRetain = {
            {1, 4},    // Segment Type (DS04)
            {9, 1},    // Directory Services Match (Y)
            {45, 16},  // First Name (QIAO)
            {79, 16},  // Street Name (FORRESTERS POINT)
        };

        // Modify the payloadc
        String modifiedPayload = modifyPayload(payload, fieldsToRetain);

        // Print the modified payload
        System.out.println("Modified Payload: " + modifiedPayload);
    }

    /**
     * Modifies the given payload by retaining only the specified fields and replacing the rest with spaces.
     *
     * @param payload        The original payload string.
     * @param fieldsToRetain A 2D array where each element is an array containing the start position and length of a field to retain.
     * @return The modified payload with only the specified fields retained.
     */
    public static String modifyPayload(String payload, int[][] fieldsToRetain) {
        // Create a StringBuilder to store the modified payload
        StringBuilder modifiedPayload = new StringBuilder(" ".repeat(payload.length()));

        // Retain only the specified fields
        for (int[] field : fieldsToRetain) {
            int start = field[0] - 1;
            int length = field[1];
            if (start >= 0 && start + length <= payload.length()) {
                String valueToRetain = payload.substring(start, start + length);
                modifiedPayload.replace(start, start + length, valueToRetain);
            }
        }

        return modifiedPayload.toString();
    }
}