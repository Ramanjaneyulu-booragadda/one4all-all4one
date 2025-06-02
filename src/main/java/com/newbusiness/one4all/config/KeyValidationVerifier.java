package com.newbusiness.one4all.config;

import jakarta.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KeyValidationVerifier {

    @Value("${rsa.private.key}")
    private String privateKeyPath;

    @Value("${rsa.public.key}")
    private String publicKeyPath;

    @PostConstruct
    public void verifyKeyPair() {
        try {
            log.info("🔐 Verifying RSA Key Pair...");
            KeyPair keyPair = KeyLoader.loadKeyPair(privateKeyPath, publicKeyPath);

            RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                    .privateKey((RSAPrivateKey) keyPair.getPrivate())
                    .keyID("dynamic-key-id")
                    .build();

            JwtEncoder encoder = new NimbusJwtEncoder((jwkSelector, context) -> jwkSelector.select(new JWKSet(rsaKey)));

            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .subject("test")
                    .claim("roles", List.of("USER"))
                    .build();

            String token = encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

            Jwt decoded = NimbusJwtDecoder
                    .withPublicKey((RSAPublicKey) keyPair.getPublic())
                    .build()
                    .decode(token);

            log.info("✅ Token successfully encoded and decoded. RSA key pair is valid!");
        } catch (Exception e) {
            log.error("❌ Key Pair verification failed:", e);
        }
    }
}
