package com.bookmarkhub.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenService {

    private static final TypeReference<Map<String, Object>> CLAIMS_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final byte[] secretBytes;
    private final Duration expiration;

    public JwtTokenService(
            ObjectMapper objectMapper,
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration:PT12H}") Duration expiration
    ) {
        this.objectMapper = objectMapper;
        this.secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.expiration = expiration;
    }

    public String generateToken(String username) {
        Instant now = Instant.now();
        String header = encodeJson(Map.of("alg", "HS256", "typ", "JWT"));
        String claims = encodeJson(Map.of(
                "sub", username,
                "iat", now.getEpochSecond(),
                "exp", now.plus(expiration).getEpochSecond()
        ));
        String unsignedToken = header + "." + claims;
        return unsignedToken + "." + sign(unsignedToken);
    }

    public Optional<String> extractUsername(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return Optional.empty();
            }

            String unsignedToken = parts[0] + "." + parts[1];
            String expectedSignature = sign(unsignedToken);
            // Keep validation minimal for the MVP: trust only signed, unexpired tokens.
            if (!MessageDigest.isEqual(
                    expectedSignature.getBytes(StandardCharsets.US_ASCII),
                    parts[2].getBytes(StandardCharsets.US_ASCII))) {
                return Optional.empty();
            }

            Map<String, Object> claims = objectMapper.readValue(
                    Base64.getUrlDecoder().decode(parts[1]),
                    CLAIMS_TYPE
            );

            Object expirationClaim = claims.get("exp");
            if (!(expirationClaim instanceof Number number)
                    || Instant.now().isAfter(Instant.ofEpochSecond(number.longValue()))) {
                return Optional.empty();
            }

            Object subject = claims.get("sub");
            if (subject instanceof String username && !username.isBlank()) {
                return Optional.of(username);
            }
            return Optional.empty();
        } catch (IllegalArgumentException | IOException ex) {
            return Optional.empty();
        }
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            return Base64.getUrlEncoder().withoutPadding().encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to serialize JWT payload", ex);
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretBytes, "HmacSHA256"));
            byte[] signature = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new IllegalStateException("Unable to sign JWT token", ex);
        }
    }
}
