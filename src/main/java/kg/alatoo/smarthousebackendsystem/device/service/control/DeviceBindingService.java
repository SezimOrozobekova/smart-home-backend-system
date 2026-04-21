package kg.alatoo.smarthousebackendsystem.device.service.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.alatoo.smarthousebackendsystem.config.MqttProperties;
import kg.alatoo.smarthousebackendsystem.device.entity.Device;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnection;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnectionType;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceProvider;
import kg.alatoo.smarthousebackendsystem.device.payload.request.BindConfirmRequest;
import kg.alatoo.smarthousebackendsystem.device.payload.request.BindInitRequest;
import kg.alatoo.smarthousebackendsystem.device.payload.response.BindInitResponse;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceConnectionResponse;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceConnectionRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceBindingService {

    private static final String CLIENT_ID_MODE = "leave_default";

    private final DeviceRepository deviceRepository;
    private final DeviceConnectionRepository deviceConnectionRepository;
    private final DeviceConnectionService deviceConnectionService;
    private final MqttProperties mqttProperties;
    private final ObjectMapper objectMapper;

    @Value("${app.mqtt.public-host:54.174.180.189}")
    private String publicMqttHost;

    @Value("${app.mqtt.public-port:1883}")
    private Integer publicMqttPort;

    public BindInitResponse initBind(UUID userId, UUID deviceId, BindInitRequest request) {
        Device device = deviceRepository.findByIdAndRoomHomeOwnerId(deviceId, userId)
                .orElseThrow(() -> new RuntimeException("Device not found or access denied"));

        validateSupported(request.provider(), request.connectionType());

        String topicPrefix = buildTopicPrefix(device.getId());

        return new BindInitResponse(
                device.getId(),
                request.provider(),
                request.connectionType(),
                publicMqttHost,
                publicMqttPort,
                mqttProperties.getUsername(),
                mqttProperties.getPassword(),
                topicPrefix,
                CLIENT_ID_MODE
        );
    }

    @Transactional
    public DeviceConnectionResponse confirmBind(UUID userId, UUID deviceId, BindConfirmRequest request) {
        Device device = deviceRepository.findByIdAndRoomHomeOwnerId(deviceId, userId)
                .orElseThrow(() -> new RuntimeException("Device not found or access denied"));

        validateSupported(request.provider(), request.connectionType());

        DeviceConnection connection = deviceConnectionRepository.findByDeviceId(deviceId)
                .orElseGet(DeviceConnection::new);

        connection.setDevice(device);
        connection.setProvider(request.provider());
        connection.setConnectionType(request.connectionType());
        connection.setExternalDeviceId(request.externalDeviceId().trim());
        connection.setConfigJson(buildShellyMqttConfigJson(device.getId()));
        connection.setCredentialsJson(buildCredentialsJson());
        connection.setLastError(null);
        connection.setIsEnabled(true);

        deviceConnectionRepository.save(connection);
        return deviceConnectionService.getByDeviceId(userId, deviceId);
    }

    private void validateSupported(DeviceProvider provider, DeviceConnectionType connectionType) {
        if (provider != DeviceProvider.SHELLY || connectionType != DeviceConnectionType.MQTT) {
            throw new RuntimeException("Only SHELLY + MQTT bind is supported for now");
        }

        if (!mqttProperties.isEnabled()) {
            throw new RuntimeException("MQTT is disabled on backend");
        }

        if (mqttProperties.getBrokerUrl() == null || mqttProperties.getBrokerUrl().isBlank()) {
            throw new RuntimeException("MQTT broker URL is not configured");
        }
    }

    private String buildTopicPrefix(UUID deviceId) {
        return "devices/" + deviceId;
    }

    private String buildShellyMqttConfigJson(UUID deviceId) {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("topicPrefix", buildTopicPrefix(deviceId));
        config.put("brokerHost", publicMqttHost);
        config.put("brokerPort", publicMqttPort);

        try {
            return objectMapper.writeValueAsString(config);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to build config JSON", e);
        }
    }

    private String buildCredentialsJson() {
        Map<String, Object> credentials = new LinkedHashMap<>();
        credentials.put("username", mqttProperties.getUsername());

        if (mqttProperties.getPassword() != null && !mqttProperties.getPassword().isBlank()) {
            credentials.put("password", mqttProperties.getPassword());
        }

        try {
            return objectMapper.writeValueAsString(credentials);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to build credentials JSON", e);
        }
    }
}