package kg.alatoo.smarthousebackendsystem.device.payload.response;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnectionType;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceProvider;

import java.util.UUID;

public record BindInitResponse(
        UUID deviceId,
        DeviceProvider provider,
        DeviceConnectionType connectionType,
        String brokerHost,
        Integer brokerPort,
        String username,
        String password,
        String topicPrefix,
        String clientIdMode
) {
}