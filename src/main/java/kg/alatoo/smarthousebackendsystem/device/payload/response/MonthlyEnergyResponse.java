package kg.alatoo.smarthousebackendsystem.device.payload.response;

import java.math.BigDecimal;

public record MonthlyEnergyResponse(
        String month,
        BigDecimal consumedWh,
        BigDecimal consumedKwh,
        BigDecimal cost
) {
}