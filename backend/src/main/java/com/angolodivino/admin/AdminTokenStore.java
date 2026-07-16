package com.angolodivino.admin;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * Opaque admin tokens held in memory only: no database, and every token dies with the process.
 */
@Component
public class AdminTokenStore {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final int TOKEN_BYTES = 32;
    private static final int MAX_ACTIVE_TOKENS = 50;

    private final Map<String, Instant> expiryByToken = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();
    private final Duration sessionTtl;

    public AdminTokenStore(AdminProperties adminProperties) {
        this.sessionTtl = adminProperties.getSessionTtl();
    }

    public AdminSession issue() {
        purgeExpired();

        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        Instant expiresAt = Instant.now().plus(sessionTtl);
        expiryByToken.put(token, expiresAt);

        enforceMaxTokens();
        return new AdminSession(token, expiresAt);
    }

    /**
     * @return the expiry of a live token, or empty when the token is unknown or expired.
     */
    public Optional<Instant> validate(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        Instant expiresAt = expiryByToken.get(token);
        if (expiresAt == null) {
            return Optional.empty();
        }
        if (expiresAt.isBefore(Instant.now())) {
            expiryByToken.remove(token);
            return Optional.empty();
        }
        return Optional.of(expiresAt);
    }

    public void revoke(String token) {
        if (token != null) {
            expiryByToken.remove(token);
        }
    }

    public static String bearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }

    private void purgeExpired() {
        Instant now = Instant.now();
        expiryByToken.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }

    /** Keeps repeated logins from growing the map without bound; the oldest sessions give way. */
    private void enforceMaxTokens() {
        int excess = expiryByToken.size() - MAX_ACTIVE_TOKENS;
        if (excess <= 0) {
            return;
        }

        expiryByToken.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .limit(excess)
                .map(Map.Entry::getKey)
                .toList()
                .forEach(expiryByToken::remove);
    }
}
