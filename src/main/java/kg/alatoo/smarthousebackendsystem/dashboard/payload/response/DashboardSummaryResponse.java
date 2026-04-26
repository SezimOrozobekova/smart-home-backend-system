package kg.alatoo.smarthousebackendsystem.dashboard.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {

    private long devicesConnected;
    private long activeDevices;
    private long roomsMonitored;

    private BigDecimal consumedWh;
    private BigDecimal consumedKwh;
    private BigDecimal estimatedMonthlyCost;
    private BigDecimal costDifferenceFromLastMonth;
}