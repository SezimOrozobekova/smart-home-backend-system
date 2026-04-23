package kg.alatoo.smarthousebackendsystem.device.service.control;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnection;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnectionType;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceProvider;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceState;
import kg.alatoo.smarthousebackendsystem.device.service.mqtt.producer.ShellyMqttCommandProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShellyMqttToggleHandler implements DeviceToggleHandler {

    private final ShellyMqttCommandProducer shellyMqttCommandProducer;

    @Override
    public boolean supports(DeviceConnection connection) {
        return connection.getProvider() == DeviceProvider.SHELLY
                && connection.getConnectionType() == DeviceConnectionType.MQTT;
    }

    @Override
    @Transactional
    public DeviceState toggle(DeviceState state, DeviceConnection connection, boolean desiredOn) {
        validate(connection);
        return shellyMqttCommandProducer.sendSwitchSet(state, connection, desiredOn);
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
}