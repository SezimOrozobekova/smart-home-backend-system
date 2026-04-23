package kg.alatoo.smarthousebackendsystem.device.service.mqtt.consumer;

import jakarta.annotation.PostConstruct;
import kg.alatoo.smarthousebackendsystem.device.service.mqtt.MqttService;
import kg.alatoo.smarthousebackendsystem.device.service.mqtt.service.ShellyMqttResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShellyMqttResponseConsumer {

    private final MqttService mqttService;
    private final ShellyMqttResponseService responseService;

    @PostConstruct
    public void register() {
        mqttService.registerListener(this::onMessage);
    }

    public void onMessage(String topic, String payload) {
        if (!"smarthouse-backend/rpc".equals(topic)) {
            return;
        }

        responseService.handle(topic, payload);
    }
}