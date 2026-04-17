package kg.alatoo.smarthousebackendsystem.dashboard.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardEnergySummary {
    private double estimatedMonthlyCost;
    private double costDifferenceFromLastMonth;
}