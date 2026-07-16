package com.angolodivino.admin;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * Fixed-window brute-force guard for the single-password login endpoint.
 */
@Component
public class AdminLoginThrottle {

    private static final int MAX_FAILURES = 5;
    private static final Duration WINDOW = Duration.ofMinutes(5);
    private static final int MAX_TRACKED_CLIENTS = 1000;

    private record Failures(int count, Instant windowStart) {
    }

    private final Map<String, Failures> failuresByClient = new ConcurrentHashMap<>();

    public boolean isBlocked(String client) {
        Failures failures = failuresByClient.get(client);
        if (failures == null) {
            return false;
        }
        if (isStale(failures, Instant.now())) {
            failuresByClient.remove(client);
            return false;
        }
        return failures.count() >= MAX_FAILURES;
    }

    public void recordFailure(String client) {
        Instant now = Instant.now();
        purgeStale(now);
        failuresByClient.merge(client, new Failures(1, now), (existing, fresh) ->
                isStale(existing, now) ? fresh : new Failures(existing.count() + 1, existing.windowStart()));
    }

    public void reset(String client) {
        failuresByClient.remove(client);
    }

    private static boolean isStale(Failures failures, Instant now) {
        return failures.windowStart().plus(WINDOW).isBefore(now);
    }

    private void purgeStale(Instant now) {
        if (failuresByClient.size() < MAX_TRACKED_CLIENTS) {
            return;
        }
        failuresByClient.entrySet().removeIf(entry -> isStale(entry.getValue(), now));
    }
}
