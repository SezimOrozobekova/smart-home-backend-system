package kg.alatoo.smarthousebackendsystem.device.repository.projection;

import java.math.BigDecimal;

public interface EnergyChartPointProjection {
    String getLabel();
    BigDecimal getConsumedWh();
}