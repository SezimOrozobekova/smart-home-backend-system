package kg.alatoo.smarthousebackendsystem.device.entity;

import jakarta.persistence.*;
import kg.alatoo.smarthousebackendsystem.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "energy_alerts")
@Getter
@Setter
@NoArgsConstructor
public class EnergyAlert {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false)
    private EnergyAlertType alertType;

    @Column(nullable = false)
    private String message;

    @Column(name = "today_kwh", precision = 10, scale = 3)
    private BigDecimal todayKwh;

    @Column(name = "average_kwh", precision = 10, scale = 3)
    private BigDecimal averageKwh;

    @Column(name = "difference_percent", precision = 10, scale = 2)
    private BigDecimal differencePercent;

    @Column(name = "alert_date", nullable = false)
    private LocalDate alertDate;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}