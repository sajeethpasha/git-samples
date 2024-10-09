public class PayloadTextReplacer {
    public static void main(String[] args) {
        String payload = "DS03139M YNNNNNMNNNNNMNHHABERER           QIAO            AELWEN     78      FORRESTERS POINT         ST MARYS        NLA0B3H0";
        
        String updatedPayload = replaceText(payload, 8, 7, "ABCDEFG");
        System.out.println(updatedPayload);
    }

    /**
     * Replaces a part of the payload text with a new replacement string.
     *
     * @param payload      The original payload string.
     * @param position     The starting position (0-based index) where replacement should start.
     * @param length       The length of the text to be replaced.
     * @param replacement  The replacement text.
     * @return             The updated payload string.
     */
    public static String replaceText(String payload, int position, int length, String replacement) {
        if (position < 0 || length < 0 || position + length > payload.length()) {
            throw new IllegalArgumentException("Position and length are out of bounds of the payload.");
        }

        return new StringBuilder(payload)
                .replace(position, position + length, replacement)
                .toString();
    }
}