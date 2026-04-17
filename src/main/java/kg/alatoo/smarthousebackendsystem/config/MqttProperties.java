package kg.alatoo.smarthousebackendsystem.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "mqtt")
public class MqttProperties {

    private boolean enabled;
    private String brokerUrl;
    private String clientId;
    private String username;
    private String password;
    private boolean autoReconnect = true;
    private boolean cleanSession = true;
}