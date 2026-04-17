package kg.alatoo.smarthousebackendsystem.dashboard.service;

import kg.alatoo.smarthousebackendsystem.dashboard.payload.response.DashboardEnergySummary;
import kg.alatoo.smarthousebackendsystem.dashboard.payload.response.DashboardSummaryResponse;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceEnergyHistoryRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceStateRepository;
import kg.alatoo.smarthousebackendsystem.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final BigDecimal PRICE_PER_KWH = new BigDecimal("0.10");
    private static final BigDecimal WH_IN_KWH = new BigDecimal("1000");

    private final DeviceRepository deviceRepository;
    private final DeviceStateRepository deviceStateRepository;
    private final RoomRepository roomRepository;
    private final DeviceEnergyHistoryRepository deviceEnergyHistoryRepository;

    public DashboardSummaryResponse getSummary(UUID userId) {
        long devicesConnected = deviceRepository.countByRoomHomeOwnerId(userId);
        long activeDevices = deviceStateRepository.countByDeviceRoomHomeOwnerIdAndIsOnTrue(userId);
        long roomsMonitored = roomRepository.countByHomeOwnerId(userId);

        DashboardEnergySummary energySummary = getCachedEnergySummary(userId);

        return DashboardSummaryResponse.builder()
                .devicesConnected(devicesConnected)
                .activeDevices(activeDevices)
                .roomsMonitored(roomsMonitored)
                .estimatedMonthlyCost(energySummary.getEstimatedMonthlyCost())
                .costDifferenceFromLastMonth(energySummary.getCostDifferenceFromLastMonth())
                .build();
    }

    @Cacheable(value = "dashboardEnergy", key = "#userId")
    public DashboardEnergySummary getCachedEnergySummary(UUID userId) {
        ZoneId zoneId = ZoneId.systemDefault();

        YearMonth currentMonth = YearMonth.now(zoneId);
        YearMonth previousMonth = currentMonth.minusMonths(1);

        Instant currentFrom = currentMonth.atDay(1).atStartOfDay(zoneId).toInstant();
        Instant currentTo = currentMonth.plusMonths(1)
                .atDay(1)
                .atStartOfDay(zoneId)
                .minusNanos(1)
                .toInstant();

        Instant previousFrom = previousMonth.atDay(1).atStartOfDay(zoneId).toInstant();
        Instant previousTo = previousMonth.plusMonths(1)
                .atDay(1)
                .atStartOfDay(zoneId)
                .minusNanos(1)
                .toInstant();

        BigDecimal currentConsumedWh = deviceEnergyHistoryRepository
                .calculateMonthlyConsumptionWhByUserId(userId, currentFrom, currentTo);

        BigDecimal previousConsumedWh = deviceEnergyHistoryRepository
                .calculateMonthlyConsumptionWhByUserId(userId, previousFrom, previousTo);

        if (currentConsumedWh == null) {
            currentConsumedWh = BigDecimal.ZERO;
        }
        if (previousConsumedWh == null) {
            previousConsumedWh = BigDecimal.ZERO;
        }

        BigDecimal currentCost = currentConsumedWh
                .divide(WH_IN_KWH, 6, RoundingMode.HALF_UP)
                .multiply(PRICE_PER_KWH)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal previousCost = previousConsumedWh
                .divide(WH_IN_KWH, 6, RoundingMode.HALF_UP)
                .multiply(PRICE_PER_KWH)
                .setScale(2, RoundingMode.HALF_UP);

        return new DashboardEnergySummary(
                currentCost.doubleValue(),
                currentCost.subtract(previousCost).doubleValue()
        );
    }
}