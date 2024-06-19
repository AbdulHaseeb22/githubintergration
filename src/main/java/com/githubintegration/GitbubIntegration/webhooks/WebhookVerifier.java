package com.githubintegration.GitbubIntegration.webhooks;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class WebhookVerifier {
    public static boolean verifySignature(String payload, String signature, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(payload.getBytes());

            String expectedSignature = "sha256=" + Base64.getEncoder().encodeToString(hash);
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify webhook signature", e);
        }
    }
}
