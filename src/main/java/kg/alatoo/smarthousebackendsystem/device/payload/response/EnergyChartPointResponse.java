package kg.alatoo.smarthousebackendsystem.device.payload.response;

import java.math.BigDecimal;

public record EnergyChartPointResponse(
        String label,
        BigDecimal consumedWh,
        BigDecimal consumedKwh,
        BigDecimal cost
) {
}