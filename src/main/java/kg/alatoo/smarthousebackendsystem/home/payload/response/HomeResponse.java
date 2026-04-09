package kg.alatoo.smarthousebackendsystem.home.payload.response;

import java.time.Instant;
import java.util.UUID;

public record HomeResponse(
        UUID id,
        String name,
        String address,
        UUID ownerId,
        Instant createdAt,
        Instant updatedAt
) {
}