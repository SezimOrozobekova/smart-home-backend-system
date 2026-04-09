package kg.alatoo.smarthousebackendsystem.room.payload.response;

import java.time.Instant;
import java.util.UUID;

public record RoomResponse(
        UUID id,
        String name,
        UUID homeId,
        Instant createdAt,
        Instant updatedAt
) {
}