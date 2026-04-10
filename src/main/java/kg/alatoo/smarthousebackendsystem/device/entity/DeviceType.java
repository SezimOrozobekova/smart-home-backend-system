package kg.alatoo.smarthousebackendsystem.device.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "device_types")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class DeviceType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private DeviceCategory category;

    @Column(name = "icon", length = 100)
    private String icon;

    @Column(name = "is_controllable", nullable = false)
    private Boolean isControllable;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}