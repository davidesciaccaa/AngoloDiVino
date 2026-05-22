package com.angolodivino.status;

import java.time.Instant;

public record ApiStatusResponse(
        String service,
        String status,
        Instant timestamp
) {
}
