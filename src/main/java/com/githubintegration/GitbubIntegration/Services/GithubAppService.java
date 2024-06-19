package com.githubintegration.GitbubIntegration.Services;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GithubAppService {
    @Autowired
    private GitHub github;

    public void processWebhookPayload(String payload) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(payload);

        String action = jsonNode.get("action").asText();
        String repositoryName = jsonNode.get("repository").get("full_name").asText();
        String owner = jsonNode.get("repository").get("owner").get("login").asText();

        // Process the repository data
        GHRepository repository = getRepository(owner, repositoryName);
        // Add your logic to handle the repository data
    }

    public GHRepository getRepository(String owner, String repo) throws IOException {
        return github.getRepository(owner + "/" + repo);
    }

    public void handleInstallation(int installationId, String repositoryName) throws IOException {
        GHAppInstallation installation = github.getApp().getInstallationById(installationId);
        GHAppInstallationToken installationToken = installation.createToken().create();
        String token = installationToken.getToken();

        GitHub installationGitHub = new GitHubBuilder().withOAuthToken(token).build();

        GHRepository repository = installationGitHub.getRepository(repositoryName);
        System.out.println("Repository Name: " + repository.getFullName());
        System.out.println("Repository Description: " + repository.getDescription());
        // Add your logic to handle the repository data
    }

    public void processRepository(int installationId, String repositoryName) {
        try {
            GHAppInstallation installation = github.getApp().getInstallationById(installationId);
            String token = installation.createToken().create().getToken();
            GitHub installationGitHub = new GitHubBuilder().withOAuthToken(token).build();

            GHRepository repository = installationGitHub.getRepository(repositoryName);
            System.out.println("Repository Name: " + repository.getFullName());
            System.out.println("Repository Description: " + repository.getDescription());
            // Add your logic to handle the repository data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
