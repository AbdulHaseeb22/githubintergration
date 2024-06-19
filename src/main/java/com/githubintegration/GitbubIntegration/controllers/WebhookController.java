package com.githubintegration.GitbubIntegration.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.githubintegration.GitbubIntegration.Services.GithubAppService;
import com.githubintegration.GitbubIntegration.webhooks.WebhookVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/webhook")
public class WebhookController {

    @Value("${github.webhook.secret}")
    private String webhookSecret;

    @Autowired
    private GithubAppService githubAppService;

    @PostMapping
    public void handleWebhook(@RequestBody String payload, @RequestHeader Map<String, String> headers) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(payload);
        String eventType = headers.get("X-GitHub-Event");

        System.out.println("Webhook received!");
        System.out.println("Payload: " + payload);
        System.out.println("Headers: " + headers);

        if ("installation".equals(eventType)) {
            System.out.println("in Installation event");
            handleInstallationEvent(jsonNode);
        } else if ("installation_repositories".equals(eventType)) {
            System.out.println("in Installation Repo event");
            handleInstallationRepositoriesEvent(jsonNode);
        }
    }

    private void handleInstallationEvent(JsonNode jsonNode) {
        int installationId = jsonNode.get("installation").get("id").asInt();
        JsonNode repositories = jsonNode.get("repositories");
        if (repositories != null) {
            for (JsonNode repo : repositories) {
                String repoName = repo.get("full_name").asText();
                System.out.println("Assigned repository: " + repoName);
                githubAppService.processRepository(installationId, repoName);
            }
        }
    }

    private void handleInstallationRepositoriesEvent(JsonNode jsonNode) {
        int installationId = jsonNode.get("installation").get("id").asInt();
        JsonNode addedRepositories = jsonNode.get("repositories_added");
        for (JsonNode repo : addedRepositories) {
            String repoName = repo.get("full_name").asText();
            System.out.println("Added repository: " + repoName);
            githubAppService.processRepository(installationId, repoName);
        }
    }
}
