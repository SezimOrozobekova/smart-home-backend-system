package kg.alatoo.smarthousebackendsystem.device.payload.response;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthlyEnergyResponse(
        String month,
        BigDecimal firstTotalWh,
        BigDecimal lastTotalWh,
        BigDecimal consumedWh,
        BigDecimal consumedKwh
) {
}