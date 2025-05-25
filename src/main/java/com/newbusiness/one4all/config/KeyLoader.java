package com.newbusiness.one4all.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

@Slf4j
public class KeyLoader {
	private static String readPemContent(InputStream inputStream) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines()
                    .filter(line -> !line.startsWith("-----"))
                    .collect(Collectors.joining());
        }
    }
	public static PrivateKey loadPrivateKey(String privateKeyPath) {
        try (InputStream inputStream = new ClassPathResource(privateKeyPath).getInputStream()) {
            String keyContent = readPemContent(inputStream);
            byte[] keyBytes = java.util.Base64.getDecoder().decode(keyContent);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load private key from path: " + privateKeyPath, e);
        }
    }

    public static PublicKey loadPublicKey(String publicKeyPath) {
        try (InputStream inputStream = new ClassPathResource(publicKeyPath).getInputStream()) {
            String keyContent = readPemContent(inputStream);
            byte[] keyBytes = java.util.Base64.getDecoder().decode(keyContent);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load public key from path: " + publicKeyPath, e);
        }
    }

    public static KeyPair loadKeyPair(String privateKeySource, String publicKeySource) throws IOException, Exception {
    	log.debug("🔐 [DEBUG] Reading public key from: " + publicKeySource);
    	log.debug("🔐 [DEBUG] Reading private key from: " + privateKeySource);

    	String publicKeyContent = readPemContent(new ClassPathResource(publicKeySource).getInputStream());
    	String privateKeyContent = readPemContent(new ClassPathResource(privateKeySource).getInputStream());

    	log.debug("🔐 Public Key Content:\n" + publicKeyContent);
    	log.debug("🔐 Private Key Content:\n" + privateKeyContent);

        return new KeyPair(loadPublicKey(publicKeySource), loadPrivateKey(privateKeySource));
    }
}

