package kg.alatoo.smarthousebackendsystem.device.service.mqtt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnection;
import kg.alatoo.smarthousebackendsystem.device.entity.ShellyStatusSnapshot;
import kg.alatoo.smarthousebackendsystem.device.payload.config.ShellyMqttConfig;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceConnectionRepository;
import kg.alatoo.smarthousebackendsystem.device.service.connection.DeviceConnectionConfigService;
import kg.alatoo.smarthousebackendsystem.device.service.telemetry.cache.DeviceTelemetryCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShellyMqttTelemetryService {

    private final ObjectMapper objectMapper;
    private final DeviceConnectionRepository deviceConnectionRepository;
    private final DeviceConnectionConfigService deviceConnectionConfigService;
    private final DeviceTelemetryCache deviceTelemetryCache;

    public void handle(String topic, String payload) {
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
            if (switchNode.isMissingNode()) {
                return;
            }

            Boolean isOn = switchNode.has("output")
                    ? switchNode.get("output").asBoolean()
                    : null;

            BigDecimal powerWatts = switchNode.has("apower") && switchNode.get("apower").isNumber()
                    ? switchNode.get("apower").decimalValue()
                    : null;

            BigDecimal current = switchNode.has("current") && switchNode.get("current").isNumber()
                    ? switchNode.get("current").decimalValue()
                    : null;

            BigDecimal totalEnergyWh = null;
            if (switchNode.has("aenergy")) {
                JsonNode aenergyNode = switchNode.get("aenergy");
                if (aenergyNode.has("total") && aenergyNode.get("total").isNumber()) {
                    totalEnergyWh = aenergyNode.get("total").decimalValue();
                }
            }

            ShellyStatusSnapshot snapshot = new ShellyStatusSnapshot(
                    isOn,
                    powerWatts,
                    null,
                    current,
                    totalEnergyWh,
                    null
            );

            deviceTelemetryCache.put(connection.getDevice().getId(), snapshot);

            log.info("Telemetry cache updated for deviceId={}", connection.getDevice().getId());

        } catch (Exception e) {
            log.error("Failed to process telemetry topic={}, payload={}", topic, payload, e);
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