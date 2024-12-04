package com.example.webhook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signatureHeader) {

        // Log the payload for debugging
        logger.info("Received webhook payload: {}", payload);

        // Validate the payload if necessary (e.g., check signatures)
        if (signatureHeader != null) {
            logger.info("Received signature: {}", signatureHeader);
            // Implement signature validation here if required
        }

        // Process the payload
        // Example: Parse JSON and take action based on the event type

        // Respond with a 200 OK to acknowledge receipt
        return new ResponseEntity<>("Webhook received successfully", HttpStatus.OK);
    }
}
