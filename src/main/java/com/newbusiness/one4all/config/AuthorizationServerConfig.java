package com.newbusiness.one4all.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class AuthorizationServerConfig {

    @Value("${microservice.url}")
    private String microServiceUrl;

    @Value("${microservice.clientid}")
    private String clientID;

    @Value("${microservice.clientsecret}")
    private String clientSecret;

    @Value("${cors.allowed.origins}")
    private String frontEndUrl;

    @Value("#{'${microservice.scope}'.split(',')}")
    private List<String> scopeList;
    @Value("${rsa.private.key}")
    private String privateKeySource;

    @Value("${rsa.public.key}")
    private String publicKeySource;

    @Bean
    public KeyPair rsaKeyPair() {
        return KeyLoader.loadKeyPair(privateKeySource, publicKeySource);
    }
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        RegisteredClient registeredClient = RegisteredClient.withId(clientID)
            .clientId(clientID)
            .clientSecret(passwordEncoder.encode(clientSecret)) // Encode the client secret
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS) // Add more grant types if needed
            .scopes(scopes -> scopes.addAll(scopeList)) // Add all scopes from the property
            .redirectUri(frontEndUrl + "/login/oauth2/code/spring") // Redirect URI for your frontend
            .tokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofHours(1))
                .refreshTokenTimeToLive(Duration.ofDays(30))
                .build())
            .build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
            .issuer(microServiceUrl) // Set the server's base URL
            .build();
    }

    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        return http.build();
    }

    @Bean
    public JwtEncoder jwtEncoder(KeyPair rsaKeyPair) {
        JWK jwk = new RSAKey.Builder((RSAPublicKey) rsaKeyPair.getPublic())
            .privateKey((RSAPrivateKey) rsaKeyPair.getPrivate())
            .keyID("dynamic-key-id")
            .build();
        JWKSource<SecurityContext> jwkSource = (jwkSelector, securityContext) -> jwkSelector.select(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(KeyPair rsaKeyPair) {
        return NimbusJwtDecoder.withPublicKey((RSAPublicKey) rsaKeyPair.getPublic()).build();
    }

    
}
