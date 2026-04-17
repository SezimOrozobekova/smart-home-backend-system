package kg.alatoo.smarthousebackendsystem.device.service.control;

import jakarta.annotation.PreDestroy;
import kg.alatoo.smarthousebackendsystem.config.MqttProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqttService {

    private final MqttProperties mqttProperties;

    private MqttClient mqttClient;

    public synchronized void publish(String topic, String payload) {
        if (!mqttProperties.isEnabled()) {
            throw new RuntimeException("MQTT is disabled");
        }

        try {
            ensureConnected();

            MqttMessage message = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
            message.setQos(1);

            mqttClient.publish(topic, message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish MQTT message", e);
        }
    }

    public synchronized boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    private void ensureConnected() throws MqttException {
        if (mqttClient != null && mqttClient.isConnected()) {
            return;
        }

        if (mqttClient == null) {
            mqttClient = new MqttClient(
                    mqttProperties.getBrokerUrl(),
                    mqttProperties.getClientId()
            );
        }

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(mqttProperties.isAutoReconnect());
        options.setCleanSession(mqttProperties.isCleanSession());

        if (mqttProperties.getUsername() != null && !mqttProperties.getUsername().isBlank()) {
            options.setUserName(mqttProperties.getUsername());
            options.setPassword(
                    mqttProperties.getPassword() != null
                            ? mqttProperties.getPassword().toCharArray()
                            : new char[0]
            );
        }

        log.info("Connecting to MQTT broker at {}", mqttProperties.getBrokerUrl());
        mqttClient.connect(options);
        log.info("MQTT connected");
    }

    @PreDestroy
    public synchronized void shutdown() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
        } catch (Exception e) {
            log.warn("Failed to disconnect MQTT client cleanly", e);
        }
    }
}