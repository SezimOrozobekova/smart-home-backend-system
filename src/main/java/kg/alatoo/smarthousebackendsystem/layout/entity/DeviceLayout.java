package kg.alatoo.smarthousebackendsystem.layout.entity;

import jakarta.persistence.*;
import kg.alatoo.smarthousebackendsystem.device.entity.Device;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "device_layouts")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class DeviceLayout {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false, unique = true)
    private Device device;

    @Column(name = "position_x", nullable = false)
    private Double positionX;

    @Column(name = "position_y", nullable = false)
    private Double positionY;

    @Column(name = "position_z", nullable = false)
    private Double positionZ;

    @Column(name = "rotation_x", nullable = false)
    private Double rotationX;

    @Column(name = "rotation_y", nullable = false)
    private Double rotationY;

    @Column(name = "rotation_z", nullable = false)
    private Double rotationZ;

    @Column(name = "scale_x", nullable = false)
    private Double scaleX;

    @Column(name = "scale_y", nullable = false)
    private Double scaleY;

    @Column(name = "scale_z", nullable = false)
    private Double scaleZ;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}