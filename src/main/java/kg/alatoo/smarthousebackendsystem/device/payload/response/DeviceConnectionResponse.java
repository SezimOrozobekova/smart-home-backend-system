package kg.alatoo.smarthousebackendsystem.device.payload.response;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnectionType;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceProvider;

import java.time.Instant;
import java.util.UUID;

public record DeviceConnectionResponse(
        UUID id,
        UUID deviceId,
        DeviceProvider provider,
        DeviceConnectionType connectionType,

        String ipAddress,
        Integer port,
        String username,
        String externalId,

        String mqttBrokerHost,
        Integer mqttBrokerPort,
        String mqttUsername,
        String mqttTopicPrefix,
        Boolean mqttUseTls,

        Boolean isEnabled,
        Instant createdAt,
        Instant updatedAt
) {
}