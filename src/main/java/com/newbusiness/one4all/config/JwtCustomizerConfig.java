package com.newbusiness.one4all.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

/**
 * Injects custom claims into the JWT access token.
 */
@Configuration
@Slf4j
public class JwtCustomizerConfig {

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> clientIdTokenCustomizer() {
        return context -> {
            if ("access_token".equals(context.getTokenType().getValue())) {
                if (context.getPrincipal() instanceof OAuth2ClientAuthenticationToken clientAuth) {
                    // Extract client_id from the authenticated principal
                    String clientId = clientAuth.getName();

                    // Inject it as a claim
                    context.getClaims().claim("client_id", clientId);
                }
            }
        };
    }
}
