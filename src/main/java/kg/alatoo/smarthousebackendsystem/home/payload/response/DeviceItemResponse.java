package kg.alatoo.smarthousebackendsystem.home.payload.response;

import java.time.Instant;
import java.util.UUID;

public record DeviceItemResponse(
        UUID id,
        String name,
        String type,
        String room,
        Integer power,
        Integer basePower,
        Boolean active,
        Boolean isOn,
        Boolean online,
        Instant updatedAt
) {
}