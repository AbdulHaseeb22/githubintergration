package com.githubintegration.GitbubIntegration.controllers;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/v1/github")
public class GithubAppController {
    @Value("${github.app.id}")
    private String githubAppId;

    @GetMapping("/install")
    public RedirectView installGitHubApp() {
        String redirectUrl = "https://github.com/apps/repositoryaccess-test-app/installations/new";
        return new RedirectView(redirectUrl);
    }
}
