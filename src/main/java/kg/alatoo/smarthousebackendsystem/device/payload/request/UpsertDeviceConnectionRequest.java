package kg.alatoo.smarthousebackendsystem.device.payload.request;

import jakarta.validation.constraints.NotNull;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnectionType;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceProvider;

public record UpsertDeviceConnectionRequest(
        @NotNull DeviceProvider provider,
        @NotNull DeviceConnectionType connectionType,

        String ipAddress,
        Integer port,
        String username,
        String password,
        String externalId,

        String mqttBrokerHost,
        Integer mqttBrokerPort,
        String mqttUsername,
        String mqttPassword,
        String mqttTopicPrefix,
        Boolean mqttUseTls,

        Boolean isEnabled
) {
}