package kg.alatoo.smarthousebackendsystem.device.payload.response;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceCommandStatus;

import java.time.Instant;
import java.util.UUID;

public record DeviceCommandResponse(
        UUID id,
        UUID deviceId,
        UUID issuedBy,
        String command,
        String payload,
        DeviceCommandStatus status,
        Instant issuedAt,
        Instant completedAt
) {
}