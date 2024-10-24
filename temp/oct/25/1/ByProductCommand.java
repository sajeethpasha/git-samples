import java.util.HashMap;
import java.util.Map;

public class ByProductCommand {
    private String name;
    private String timingMap;
    private String defaultCommand;
    private Map<String, String> productMappings;

    // Constructor
    public ByProductCommand(String name, String timingMap, String defaultCommand) {
        this.name = name;
        this.timingMap = timingMap;
        this.defaultCommand = defaultCommand;
        this.productMappings = new HashMap<>();
    }

    // Method to add product mappings
    public void addProductMapping(String productCode, String commandValue) {
        productMappings.put(productCode, commandValue);
    }

    // Method to execute a command based on the product code
    public void executeCommand(String productCode) {
        String command = productMappings.getOrDefault(productCode, defaultCommand);
        System.out.println("Executing command: " + command);
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getTimingMap() {
        return timingMap;
    }

    public String getDefaultCommand() {
        return defaultCommand;
    }

    public Map<String, String> getProductMappings() {
        return productMappings;
    }

    public static void main(String[] args) {
        // Example usage based on the XML configuration
        ByProductCommand command1 = new ByProductCommand("output_arpt_ffr", "dev", "arpt_ffr_subject_default");
        command1.addProductMapping("011", "arpt_ffr_subject_ascs");
        command1.addProductMapping("012", "arpt_ffr_subject_asid");
        command1.addProductMapping("901", "arpt_ffr_rbdep");
        command1.addProductMapping("934", "arpt_ffr_subject_gehbc");
        command1.addProductMapping("D34", "arpt_ffr_subject_gehbc");

        // Execute command based on product code
        command1.executeCommand("011"); // Output: Executing command: arpt_ffr_subject_ascs
        command1.executeCommand("999"); // Output: Executing command: arpt_ffr_subject_default

        // Example for the second command
        ByProductCommand command2 = new ByProductCommand("output_arpt_ffr_rboi", "dev", "arpt_ffr_rbdep_rboi");
        command2.addProductMapping("901", "arpt_ffr_rbdep_rboi");

        // Execute command based on product code
        command2.executeCommand("901"); // Output: Executing command: arpt_ffr_rbdep_rboi
        command2.executeCommand("999"); // Output: Executing command: arpt_ffr_rbdep_rboi
    }
}
