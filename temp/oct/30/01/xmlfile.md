# XML Command Flow Overview

The XML fragments provided across the seven images are interconnected in a sequential and logical flow. Here's how each part links to the others:

## Flow Description

### 1. First XML (soln_tu4i)
The first XML snippet contains a command named `soln_tu4i`, which is part of the reference `object manager.command`. It references `xmlToOutputCommand` set to `"output"`, which is key to moving on to the next command.

### 2. Second XML (output)
The command name is `"output"`, referenced from `soln_tu4i`. It points to the next stage in the flow using `defaultOutputCommand="output_tu4r_ffr"`. This command also defines attributes such as `outputTypeMap` and `timingMap`, which are used in determining specific configurations.

### 3. Third XML (output_tu4r_ffr)
The next command is `"output_tu4r_ffr"`, as indicated by the `defaultOutputCommand` in the previous XML. It further includes a `param` with a product value (`09989=output_tu4r_ffr_Maxium`), establishing the link to the subsequent command configuration `"output_tu4r_ffr_Maxium"`.

### 4. Fourth XML (output_tu4r_ffr_Maxium)
The command name is `"output_tu4r_ffr_Maxium"`, which links from the `param` value in the previous XML. It includes multiple parameters (`commandList`) with values that point to the header, subject, and trailer components: `"tu4r_ffr_header_default"`, `"tu4r_ffr_subject_Maxium"`, and `"tu4r_ffr_trailer_default"`. These values lead to specific commands defined later in the XML structure.

### 5. Fifth XML (tu4r_ffr_header_default)
This is the command for the header, `"tu4r_ffr_header_default"`, as specified in the `commandList` value from the previous XML. It provides header-specific configurations (Header Data), forming a part of the output process.

### 6. Sixth XML (tu4r_ffr_subject_Maxium)
This is the command for the subject, `"tu4r_ffr_subject_Maxium"`, also mentioned in the `commandList` of the fourth XML. It includes a `prprocessor` with a parameter (`usedByProduct="09989"`) indicating further product-specific customization or configuration.

### 7. Seventh XML (tu4r_ffr_trailer_default)
This is the command for the trailer, `"tu4r_ffr_trailer_default"`, which is the last in the command list from `"output_tu4r_ffr_Maxium"`. It has a `prprocessor` to finalize the trailer section of the output (`TU4R_ENDS`), concluding the command flow.

## How They Are Linked Sequentially

### Command Flow
- The sequence begins with `soln_tu4i`, which leads to the `output` command.
- `output` then references `output_tu4r_ffr` as the next stage.

### Product Configuration
- `output_tu4r_ffr` provides a reference for the product with `output_tu4r_ffr_Maxium`.
- `output_tu4r_ffr_Maxium` then specifies the structure of the output using the header, subject, and trailer commands.

### Individual Commands
- Each command (header, subject, trailer) is linked back to `output_tu4r_ffr_Maxium` and forms a cohesive output generation process.

## Summary
The XML from the images represents a hierarchical and interconnected flow of commands where each stage calls the next, forming a complete output process. This linkage allows different parts (header, subject, trailer) to be processed systematically as per the configured product or output requirements. Each XML fragment is designed to refer to the next in a chain of commands, ensuring that the required components for the process are sequentially activated and executed.

