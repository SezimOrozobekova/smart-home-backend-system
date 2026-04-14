package kg.alatoo.smarthousebackendsystem.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mqtt")
public class MqttProperties {

    private boolean enabled;
    private String host;
    private int port;
    private String clientId;
    private String username;
    private String password;
    private boolean cleanSession;
    private boolean autoReconnect;

    public String getBrokerUrl() {
        return "tcp://" + host + ":" + port;
    }
}