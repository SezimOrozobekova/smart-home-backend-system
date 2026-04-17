package kg.alatoo.smarthousebackendsystem.device.service.control;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnection;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnectionType;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceProvider;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceState;
import kg.alatoo.smarthousebackendsystem.device.payload.config.ShellyMqttConfig;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ShellyMqttToggleHandler implements DeviceToggleHandler {

    private final MqttService mqttService;
    private final DeviceStateRepository deviceStateRepository;
    private final DeviceConnectionConfigService configService;

    @Override
    public boolean supports(DeviceConnection connection) {
        return connection.getProvider() == DeviceProvider.SHELLY
                && connection.getConnectionType() == DeviceConnectionType.MQTT;
    }

    @Override
    @Transactional
    public DeviceState toggle(DeviceState state, DeviceConnection connection, boolean nextOn) {
        validate(connection);

        ShellyMqttConfig config = configService.getShellyMqttConfig(connection);

        if (config.topicPrefix() == null || config.topicPrefix().isBlank()) {
            throw new RuntimeException("MQTT topic prefix is not configured");
        }

        String topic = config.topicPrefix() + "/rpc";
        String payload = buildTogglePayload(nextOn);

        try {
            mqttService.publish(topic, payload);

            state.setIsOn(nextOn);
            state.setIsOnline(true);
            state.setLastSeenAt(Instant.now());

            return deviceStateRepository.save(state);
        } catch (Exception e) {
            state.setLastSeenAt(Instant.now());
            deviceStateRepository.save(state);
            throw new RuntimeException("Failed to publish MQTT toggle command", e);
        }
    }

    private void validate(DeviceConnection connection) {
        if (!Boolean.TRUE.equals(connection.getIsEnabled())) {
            throw new RuntimeException("Device connection is disabled");
        }

        if (connection.getProvider() != DeviceProvider.SHELLY) {
            throw new RuntimeException("Unsupported provider for Shelly MQTT handler");
        }

        if (connection.getConnectionType() != DeviceConnectionType.MQTT) {
            throw new RuntimeException("Unsupported connection type for Shelly MQTT handler");
        }
    }

    private String buildTogglePayload(boolean on) {
        return """
        {
          "id": 1,
          "src": "backend",
          "method": "Switch.Set",
          "params": {
            "id": 0,
            "on": %s
          }
        }
        """.formatted(on);
    }
}