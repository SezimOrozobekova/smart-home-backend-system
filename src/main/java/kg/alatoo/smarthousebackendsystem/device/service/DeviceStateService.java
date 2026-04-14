package kg.alatoo.smarthousebackendsystem.device.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnection;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnectionType;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceProvider;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceState;
import kg.alatoo.smarthousebackendsystem.device.mapper.DeviceStateMapper;
import kg.alatoo.smarthousebackendsystem.device.payload.request.UpdateDeviceStateRequest;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceStateResponse;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceConnectionRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceStateService {

    private final DeviceStateRepository deviceStateRepository;
    private final DeviceConnectionRepository deviceConnectionRepository;
    private final DeviceStateMapper deviceStateMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MqttService mqttService;

    public DeviceStateResponse getByDeviceId(UUID deviceId) {
        DeviceState state = deviceStateRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device state not found"));

        return deviceStateMapper.toResponse(state);
    }

    @Transactional
    public DeviceStateResponse updateByDeviceId(UUID deviceId, UpdateDeviceStateRequest request) {
        DeviceState state = deviceStateRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device state not found"));

        if (request.isOnline() != null) {
            state.setIsOnline(request.isOnline());
        }

        if (request.isOn() != null) {
            state.setIsOn(request.isOn());
        }

        if (request.powerWatts() != null) {
            state.setPowerWatts(request.powerWatts());
        }

        if (request.peakCapacityWatts() != null) {
            state.setPeakCapacityWatts(request.peakCapacityWatts());
        }

        if (request.lastSeenAt() != null) {
            state.setLastSeenAt(request.lastSeenAt());
        }

        if (request.rawState() != null) {
            state.setRawState(request.rawState());
        }

        DeviceState saved = deviceStateRepository.save(state);
        return deviceStateMapper.toResponse(saved);
    }

    @Transactional
    public DeviceStateResponse toggleDevice(UUID deviceId) {
        DeviceState state = deviceStateRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device state not found"));

        DeviceConnection connection = deviceConnectionRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device connection not found"));

        boolean nextOn = !Boolean.TRUE.equals(state.getIsOn());

        return switch (connection.getConnectionType()) {
            case MQTT -> toggleViaMqtt(state, connection, nextOn);
            case LOCAL_HTTP -> toggleViaHttp(state, connection, nextOn);
            default -> throw new RuntimeException("Unsupported connection type");
        };
    }

    private DeviceStateResponse toggleViaMqtt(
            DeviceState state,
            DeviceConnection connection,
            boolean nextOn
    ) {
        validateShellyMqtt(connection);

        String topic = connection.getMqttTopicPrefix() + "/rpc";
        String payload = buildShellyTogglePayload(nextOn);

        mqttService.publish(topic, payload);

        state.setIsOn(nextOn);
        state.setIsOnline(true);
        state.setLastSeenAt(Instant.now());

        DeviceState saved = deviceStateRepository.save(state);
        return deviceStateMapper.toResponse(saved);
    }

    private DeviceStateResponse toggleViaHttp(
            DeviceState state,
            DeviceConnection connection,
            boolean nextOn
    ) {
        validateShellyLocalHttp(connection);

        try {
            String switchUrl = buildSwitchUrl(connection, nextOn);
            restTemplate.getForObject(switchUrl, String.class);

            String statusUrl = buildStatusUrl(connection);
            String response = restTemplate.getForObject(statusUrl, String.class);

            JsonNode json = objectMapper.readTree(response);
            JsonNode switchNode = json.path("switch:0");

            boolean isOn = switchNode.path("output").asBoolean(false);
            double apower = switchNode.path("apower").asDouble(0.0);

            state.setIsOn(isOn);
            state.setIsOnline(true);
            state.setPowerWatts(BigDecimal.valueOf(apower));
            state.setLastSeenAt(Instant.now());
            state.setRawState(json);

            DeviceState saved = deviceStateRepository.save(state);
            return deviceStateMapper.toResponse(saved);

        } catch (Exception e) {
            state.setIsOnline(false);
            state.setLastSeenAt(Instant.now());
            deviceStateRepository.save(state);

            throw new RuntimeException("Failed to toggle live device", e);
        }
    }

    private void validateShellyLocalHttp(DeviceConnection connection) {
        if (!Boolean.TRUE.equals(connection.getIsEnabled())) {
            throw new RuntimeException("Device connection is disabled");
        }

        if (connection.getProvider() != DeviceProvider.SHELLY) {
            throw new RuntimeException("Only SHELLY provider is supported for now");
        }

        if (connection.getConnectionType() != DeviceConnectionType.LOCAL_HTTP) {
            throw new RuntimeException("Only LOCAL_HTTP connection is supported here");
        }

        if (connection.getIpAddress() == null || connection.getIpAddress().isBlank()) {
            throw new RuntimeException("Device IP is not configured");
        }
    }

    private void validateShellyMqtt(DeviceConnection connection) {
        if (!Boolean.TRUE.equals(connection.getIsEnabled())) {
            throw new RuntimeException("Device connection is disabled");
        }

        if (connection.getProvider() != DeviceProvider.SHELLY) {
            throw new RuntimeException("Only SHELLY provider is supported for now");
        }

        if (connection.getConnectionType() != DeviceConnectionType.MQTT) {
            throw new RuntimeException("Only MQTT connection is supported here");
        }

        if (connection.getMqttTopicPrefix() == null || connection.getMqttTopicPrefix().isBlank()) {
            throw new RuntimeException("MQTT topic prefix is not configured");
        }
    }

    private String buildShellyTogglePayload(boolean on) {
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

    private String buildStatusUrl(DeviceConnection connection) {
        return buildBaseUrl(connection) + "/rpc/Shelly.GetStatus";
    }

    private String buildSwitchUrl(DeviceConnection connection, boolean turnOn) {
        return buildBaseUrl(connection) + "/rpc/Switch.Set?id=0&on=" + turnOn;
    }

    private String buildBaseUrl(DeviceConnection connection) {
        int port = connection.getPort() != null ? connection.getPort() : 80;
        return "http://" + connection.getIpAddress() + ":" + port;
    }
}