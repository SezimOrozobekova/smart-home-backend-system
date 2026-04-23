package kg.alatoo.smarthousebackendsystem.device.service.mqtt;

import java.time.Instant;
import java.util.UUID;

public record PendingMqttCommand(
        int requestId,
        UUID deviceId,
        boolean desiredOn,
        Instant createdAt
) {
}