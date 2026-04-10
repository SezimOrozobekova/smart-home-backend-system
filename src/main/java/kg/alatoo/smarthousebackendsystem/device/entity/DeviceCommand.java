package kg.alatoo.smarthousebackendsystem.device.entity;

import kg.alatoo.smarthousebackendsystem.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "device_commands")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class DeviceCommand {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by", nullable = false)
    private User issuedBy;

    @Column(name = "command", nullable = false, length = 50)
    private String command;

    @Column(name = "payload", columnDefinition = "jsonb")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DeviceCommandStatus status;

    @CreatedDate
    @Column(name = "issued_at", nullable = false, updatable = false)
    private Instant issuedAt;

    @Column(name = "completed_at")
    private Instant completedAt;
}