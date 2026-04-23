package kg.alatoo.smarthousebackendsystem.device.service.mqtt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceState;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceStateRepository;
import kg.alatoo.smarthousebackendsystem.device.service.mqtt.PendingMqttCommand;
import kg.alatoo.smarthousebackendsystem.device.service.mqtt.PendingMqttCommandRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShellyMqttResponseService {

    private final ObjectMapper objectMapper;
    private final PendingMqttCommandRegistry pendingRegistry;
    private final DeviceStateRepository deviceStateRepository;

    @Transactional
    public void handle(String topic, String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            int requestId = root.path("id").asInt(-1);

            if (requestId < 0) {
                log.warn("MQTT response without valid request id: payload={}", payload);
                return;
            }

            PendingMqttCommand command = pendingRegistry.get(requestId).orElse(null);
            if (command == null) {
                log.warn("No pending MQTT command found for requestId={}", requestId);
                return;
            }

            DeviceState state = deviceStateRepository.findByDeviceId(command.deviceId())
                    .orElseThrow(() -> new RuntimeException("Device state not found for deviceId=" + command.deviceId()));

            state.setIsOn(command.desiredOn());
            state.setIsOnline(true);
            state.setLastSeenAt(Instant.now());
            state.setRecordedAt(Instant.now());

            deviceStateRepository.save(state);
            pendingRegistry.remove(requestId);

            log.info("Device state updated from MQTT response: deviceId={}, isOn={}",
                    command.deviceId(), command.desiredOn());

        } catch (Exception e) {
            log.error("Failed to process MQTT response topic={}, payload={}", topic, payload, e);
        }
    }
}