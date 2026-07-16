package com.angolodivino.admin;

import java.time.Instant;

public record AdminSession(String token, Instant expiresAt) {
}
