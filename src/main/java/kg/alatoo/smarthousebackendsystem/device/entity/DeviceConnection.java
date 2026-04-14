package kg.alatoo.smarthousebackendsystem.device.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "device_connections")
@Getter
@Setter
public class DeviceConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false, unique = true)
    private Device device;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 50)
    private DeviceProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "connection_type", nullable = false, length = 50)
    private DeviceConnectionType connectionType;

    // LOCAL_HTTP
    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "port")
    private Integer port;

    // optional auth
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    // universal external identifier
    @Column(name = "external_id")
    private String externalId;

    // MQTT
    @Column(name = "mqtt_broker_host")
    private String mqttBrokerHost;

    @Column(name = "mqtt_broker_port")
    private Integer mqttBrokerPort;

    @Column(name = "mqtt_username")
    private String mqttUsername;

    @Column(name = "mqtt_password")
    private String mqttPassword;

    @Column(name = "mqtt_topic_prefix")
    private String mqttTopicPrefix;

    @Column(name = "mqtt_use_tls")
    private Boolean mqttUseTls;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}