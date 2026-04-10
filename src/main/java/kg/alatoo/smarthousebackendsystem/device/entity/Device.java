package kg.alatoo.smarthousebackendsystem.device.entity;

import kg.alatoo.smarthousebackendsystem.home.entity.Home;
import kg.alatoo.smarthousebackendsystem.room.entity.Room;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "devices")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_type_id", nullable = false)
    private DeviceType deviceType;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "external_id", length = 255)
    private String externalId;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "firmware_version", length = 50)
    private String firmwareVersion;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}