package kg.alatoo.smarthousebackendsystem.device.service.mqtt;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import kg.alatoo.smarthousebackendsystem.config.MqttProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqttService {

    private final MqttProperties mqttProperties;

    private final List<MqttMessageListener> listeners = new CopyOnWriteArrayList<>();

    private MqttClient mqttClient;

    @PostConstruct
    public void init() {
        if (!mqttProperties.isEnabled()) {
            log.info("MQTT is disabled, listener startup skipped");
            return;
        }

        try {
            ensureConnected();
            subscribeInternal("devices/+/events/rpc");
            subscribeInternal("smarthouse-backend/rpc");
        } catch (Exception e) {
            log.error("Failed to initialize MQTT listener", e);
        }
    }

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

    public void registerListener(MqttMessageListener listener) {
        listeners.add(listener);
    }

    public synchronized void subscribe(String topicFilter) {
        if (!mqttProperties.isEnabled()) {
            throw new RuntimeException("MQTT is disabled");
        }
        try {
            subscribeInternal(topicFilter);
        } catch (Exception e) {
            throw new RuntimeException("Failed to subscribe to topic " + topicFilter, e);
        }
    }

    public synchronized boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    private synchronized void ensureConnected() throws MqttException {
        if (mqttClient != null && mqttClient.isConnected()) {
            return;
        }

        if (mqttClient == null) {
            mqttClient = new MqttClient(
                    mqttProperties.getBrokerUrl(),
                    mqttProperties.getClientId()
            );

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    log.warn("MQTT connection lost", cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                    log.info("MQTT message arrived topic={}, payload={}", topic, payload);

                    for (MqttMessageListener listener : listeners) {
                        try {
                            listener.onMessage(topic, payload);
                        } catch (Exception e) {
                            log.error(
                                    "Failed to process MQTT message in listener {}",
                                    listener.getClass().getSimpleName(),
                                    e
                            );
                        }
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // no-op
                }
            });
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

    private synchronized void subscribeInternal(String topicFilter) throws MqttException {
        ensureConnected();
        mqttClient.subscribe(topicFilter, 1);
        log.info("Subscribed to MQTT topic filter={}", topicFilter);
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

    public interface MqttMessageListener {
        void onMessage(String topic, String payload);
    }
}