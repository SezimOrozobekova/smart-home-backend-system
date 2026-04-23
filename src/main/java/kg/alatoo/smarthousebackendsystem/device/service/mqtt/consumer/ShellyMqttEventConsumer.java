package kg.alatoo.smarthousebackendsystem.device.service.mqtt.consumer;

import jakarta.annotation.PostConstruct;
import kg.alatoo.smarthousebackendsystem.device.service.mqtt.MqttService;
import kg.alatoo.smarthousebackendsystem.device.service.mqtt.service.ShellyMqttTelemetryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShellyMqttEventConsumer {

    private final MqttService mqttService;
    private final ShellyMqttTelemetryService telemetryService;

    @PostConstruct
    public void register() {
        mqttService.registerListener(this::onMessage);
    }

    public void onMessage(String topic, String payload) {
        if (!topic.endsWith("/events/rpc")) {
            return;
        }

        telemetryService.handle(topic, payload);
    }
}