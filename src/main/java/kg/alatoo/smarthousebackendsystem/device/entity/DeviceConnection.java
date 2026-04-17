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

    @Column(name = "external_device_id")
    private String externalDeviceId;

    @Column(name = "config_json", columnDefinition = "jsonb", nullable = false)
    private String configJson = "{}";

    @Column(name = "credentials_json", columnDefinition = "jsonb")
    private String credentialsJson;

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}