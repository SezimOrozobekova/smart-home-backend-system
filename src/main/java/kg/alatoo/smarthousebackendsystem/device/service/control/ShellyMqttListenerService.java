package kg.alatoo.smarthousebackendsystem.device.service.control;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnection;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceState;
import kg.alatoo.smarthousebackendsystem.device.payload.config.ShellyMqttConfig;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceConnectionRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShellyMqttListenerService {

    private final MqttService mqttService;
    private final ObjectMapper objectMapper;
    private final DeviceConnectionRepository deviceConnectionRepository;
    private final DeviceStateRepository deviceStateRepository;
    private final DeviceConnectionConfigService deviceConnectionConfigService;

    @PostConstruct
    public void register() {
        mqttService.registerListener(this::handleMessage);
    }

    @Transactional
    public void handleMessage(String topic, String payload) {
        if (!topic.endsWith("/events/rpc")) {
            return;
        }

        try {
            String topicPrefix = extractTopicPrefix(topic);
            DeviceConnection connection = findByTopicPrefix(topicPrefix);
            if (connection == null) {
                log.warn("No device connection found for topicPrefix={}", topicPrefix);
                return;
            }

            JsonNode root = objectMapper.readTree(payload);
            String method = root.path("method").asText(null);

            if (!"NotifyStatus".equals(method)) {
                return;
            }

            JsonNode params = root.path("params");
            JsonNode switchNode = params.path("switch:0");

            DeviceState state = deviceStateRepository.findByDeviceId(connection.getDevice().getId())
                    .orElseGet(() -> {
                        DeviceState newState = new DeviceState();
                        newState.setDevice(connection.getDevice());
                        return newState;
                    });

            if (!switchNode.isMissingNode()) {
                state.setIsOn(switchNode.path("output").asBoolean(false));

                JsonNode apower = switchNode.path("apower");
                if (!apower.isMissingNode() && apower.isNumber()) {
                    state.setPowerWatts(apower.decimalValue());
                }
            }

            state.setIsOnline(true);
            state.setLastSeenAt(Instant.now());
            state.setRecordedAt(Instant.now());

            deviceStateRepository.save(state);
            log.info("Updated device state from MQTT for deviceId={}", connection.getDevice().getId());

        } catch (Exception e) {
            log.error("Failed to process Shelly MQTT message topic={}", topic, e);
        }
    }

    private String extractTopicPrefix(String topic) {
        return topic.substring(0, topic.length() - "/events/rpc".length());
    }

    private DeviceConnection findByTopicPrefix(String topicPrefix) {
        return deviceConnectionRepository.findAll().stream()
                .filter(connection -> {
                    try {
                        ShellyMqttConfig config = deviceConnectionConfigService.getShellyMqttConfig(connection);
                        return topicPrefix.equals(config.topicPrefix());
                    } catch (Exception e) {
                        return false;
                    }
                })
                .findFirst()
                .orElse(null);
    }
}