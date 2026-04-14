package kg.alatoo.smarthousebackendsystem.device.entity;

import java.math.BigDecimal;

public record ShellyStatusSnapshot(
        Boolean isOn,
        BigDecimal powerWatts,
        BigDecimal voltage,
        BigDecimal current,
        BigDecimal totalEnergyWh,
        BigDecimal temperatureC
) {
}