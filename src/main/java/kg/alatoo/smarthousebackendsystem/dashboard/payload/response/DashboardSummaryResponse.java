package kg.alatoo.smarthousebackendsystem.dashboard.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private long devicesConnected;
    private long activeDevices;
    private long roomsMonitored;
    private double estimatedMonthlyCost;
    private double costDifferenceFromLastMonth;
}