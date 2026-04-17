package kg.alatoo.smarthousebackendsystem.device.service.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnection;
import kg.alatoo.smarthousebackendsystem.device.payload.config.ShellyMqttConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceConnectionConfigService {

    private final ObjectMapper objectMapper;

    public ShellyMqttConfig getShellyMqttConfig(DeviceConnection connection) {
        try {
            String json = connection.getConfigJson();

            if (json == null || json.isBlank()) {
                return new ShellyMqttConfig(null);
            }

            return objectMapper.readValue(json, ShellyMqttConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid Shelly MQTT config JSON", e);
        }
    }
}