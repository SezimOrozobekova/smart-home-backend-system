package kg.alatoo.smarthousebackendsystem.device.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "device_energy_history")
@Getter
@Setter
public class DeviceEnergyHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    @Column(name = "power_watts", precision = 10, scale = 3)
    private BigDecimal powerWatts;

    @Column(name = "voltage", precision = 10, scale = 3)
    private BigDecimal voltage;

    @Column(name = "current", precision = 10, scale = 3)
    private BigDecimal current;

    @Column(name = "total_energy_wh", precision = 14, scale = 3)
    private BigDecimal totalEnergyWh;

    @Column(name = "temperature_c", precision = 10, scale = 3)
    private BigDecimal temperatureC;
}