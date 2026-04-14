package kg.alatoo.smarthousebackendsystem.device.payload.response;

import java.math.BigDecimal;
import java.time.Instant;

public record EnergyPointResponse(
        Instant recordedAt,
        BigDecimal powerWatts,
        BigDecimal totalEnergyWh
) {
}