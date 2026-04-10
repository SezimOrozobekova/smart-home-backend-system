package kg.alatoo.smarthousebackendsystem.device.payload.response;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceCategory;

import java.time.Instant;
import java.util.UUID;

public record DeviceTypeResponse(
        UUID id,
        String code,
        String name,
        DeviceCategory category,
        String icon,
        Boolean isControllable,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
}