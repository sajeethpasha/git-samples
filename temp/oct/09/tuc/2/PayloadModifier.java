public class PayloadModifier {
    public static void main(String[] args) {
        // Original payload
        String payload = "DS04252Y20201029CAPITAL CITY SAVINGS 023975015872036502 20240423MAC DONALD AFRICA YYYYYYYYYYYY";

        // List of fields to retain, the rest will be emptied
        int[][] fieldsToRetain = {
            {0, 4},    // Segment Type (DS04)
            {8, 1},    // Directory Services Match (Y)
            {44, 16},  // First Name (QIAO)
            {78, 16},  // Street Name (FORRESTERS POINT)
        };

        // Modify the payload
        String modifiedPayload = modifyPayload(payload, fieldsToRetain);

        // Print the modified payload
        System.out.println("Modified Payload: " + modifiedPayload);
    }

    public static String modifyPayload(String payload, int[][] fieldsToRetain) {
        // Create a StringBuilder to store the modified payload
        StringBuilder modifiedPayload = new StringBuilder(payload);

        // Replace the entire payload with spaces initially
        for (int i = 0; i < payload.length(); i++) {
            modifiedPayload.setCharAt(i, ' ');
        }

        // Retain only the specified fields
        for (int[] field : fieldsToRetain) {
            int start = field[0];
            int length = field[1];
            if (start + length <= payload.length()) {
                String valueToRetain = payload.substring(start, start + length);
                modifiedPayload.replace(start, start + length, valueToRetain);
            }
        }

        return modifiedPayload.toString();
    }
}