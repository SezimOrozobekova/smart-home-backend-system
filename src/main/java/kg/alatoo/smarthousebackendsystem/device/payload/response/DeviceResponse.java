package kg.alatoo.smarthousebackendsystem.device.payload.response;

import java.time.Instant;
import java.util.UUID;

public record DeviceResponse(
        UUID id,
        UUID roomId,
        UUID deviceTypeId,
        String deviceTypeCode,
        String deviceTypeName,
        String name,
        String externalId,
        String model,
        String firmwareVersion,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
}