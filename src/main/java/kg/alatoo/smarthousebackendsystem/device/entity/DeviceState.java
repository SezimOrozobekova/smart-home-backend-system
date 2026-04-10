package kg.alatoo.smarthousebackendsystem.device.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "device_states")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class DeviceState {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false, unique = true)
    private Device device;

    @Column(name = "is_online", nullable = false)
    private Boolean isOnline;

    @Column(name = "is_on", nullable = false)
    private Boolean isOn;

    @Column(name = "power_watts", precision = 10, scale = 2)
    private BigDecimal powerWatts;

    @Column(name = "peak_capacity_watts", precision = 10, scale = 2)
    private BigDecimal peakCapacityWatts;

    @Column(name = "last_seen_at")
    private Instant lastSeenAt;

    @Column(name = "raw_state", columnDefinition = "jsonb")
    private String rawState;

    @LastModifiedDate
    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;
}