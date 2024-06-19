package com.githubintegration.GitbubIntegration.configs;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Configuration
public class GithubConfig {
    @Value("${github.app.id}")
    private String githubAppId;

    @Value("${github.private.key}")
    private String githubPrivateKey;

    @Bean
    public GitHub getGitHub() throws IOException {
        String jwtToken = createJWT(githubAppId, githubPrivateKey);
        return new GitHubBuilder()
                .withJwtToken(jwtToken)
                .build();
    }

    private String createJWT(String appId, String privateKeyContent) {
        // Clean the private key content
        privateKeyContent = privateKeyContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(privateKeyContent);

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);

            // JWT expires after 10 minutes
            long expMillis = nowMillis + 600000;
            Date exp = new Date(expMillis);

            return Jwts.builder()
                    .setIssuer(appId)
                    .setIssuedAt(now)
                    .setExpiration(exp)
                    .signWith(SignatureAlgorithm.RS256, privateKey)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create JWT token", e);
        }
    }
}
