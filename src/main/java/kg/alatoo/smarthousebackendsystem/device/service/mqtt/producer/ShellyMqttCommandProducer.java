package kg.alatoo.smarthousebackendsystem.device.service.mqtt.producer;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnection;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceState;
import kg.alatoo.smarthousebackendsystem.device.payload.config.ShellyMqttConfig;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceStateRepository;
import kg.alatoo.smarthousebackendsystem.device.service.connection.DeviceConnectionConfigService;
import kg.alatoo.smarthousebackendsystem.device.service.mqtt.MqttService;
import kg.alatoo.smarthousebackendsystem.device.service.mqtt.PendingMqttCommand;
import kg.alatoo.smarthousebackendsystem.device.service.mqtt.PendingMqttCommandRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShellyMqttCommandProducer {

    private final MqttService mqttService;
    private final DeviceConnectionConfigService configService;
    private final PendingMqttCommandRegistry pendingRegistry;
    private final DeviceStateRepository deviceStateRepository;

    private final AtomicInteger requestCounter = new AtomicInteger(1);

    @Transactional
    public DeviceState sendSwitchSet(DeviceState state, DeviceConnection connection, boolean desiredOn) {
        ShellyMqttConfig config = configService.getShellyMqttConfig(connection);

        if (config.topicPrefix() == null || config.topicPrefix().isBlank()) {
            throw new RuntimeException("MQTT topic prefix is not configured");
        }

        String topic = config.topicPrefix() + "/rpc";
        int requestId = requestCounter.getAndIncrement();
        String payload = buildPayload(requestId, desiredOn);

        try {
            pendingRegistry.put(new PendingMqttCommand(
                    requestId,
                    connection.getDevice().getId(),
                    desiredOn,
                    Instant.now()
            ));

            log.info("Publishing Shelly MQTT command: topic={}, payload={}", topic, payload);
            mqttService.publish(topic, payload);

            state.setLastSeenAt(Instant.now());
            return deviceStateRepository.save(state);

        } catch (Exception e) {
            pendingRegistry.remove(requestId);
            throw e;
        }
    }

    private String buildPayload(int requestId, boolean desiredOn) {
        return """
        {
          "id": %s,
          "src": "smarthouse-backend",
          "method": "Switch.Set",
          "params": {
            "id": 0,
            "on": %s
          }
        }
        """.formatted(requestId, desiredOn);
    }
}