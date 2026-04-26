package kg.alatoo.smarthousebackendsystem.dashboard.service;

import kg.alatoo.smarthousebackendsystem.dashboard.payload.response.DashboardSummaryResponse;
import kg.alatoo.smarthousebackendsystem.device.payload.response.MonthlyEnergyResponse;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceStateRepository;
import kg.alatoo.smarthousebackendsystem.device.service.DeviceEnergyService;
import kg.alatoo.smarthousebackendsystem.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DeviceRepository deviceRepository;
    private final DeviceStateRepository deviceStateRepository;
    private final RoomRepository roomRepository;
    private final DeviceEnergyService deviceEnergyService;

    public DashboardSummaryResponse getSummary(UUID userId) {
        long devicesConnected = deviceRepository.countByRoomHomeOwnerId(userId);
        long activeDevices = deviceStateRepository.countByDeviceRoomHomeOwnerIdAndIsOnTrue(userId);
        long roomsMonitored = roomRepository.countByHomeOwnerId(userId);

        ZoneId zoneId = ZoneId.systemDefault();

        YearMonth currentMonth = YearMonth.now(zoneId);
        YearMonth previousMonth = currentMonth.minusMonths(1);

        MonthlyEnergyResponse currentEnergy = deviceEnergyService.getMonthlyConsumptionByUser(
                userId,
                currentMonth,
                zoneId
        );

        MonthlyEnergyResponse previousEnergy = deviceEnergyService.getMonthlyConsumptionByUser(
                userId,
                previousMonth,
                zoneId
        );

        BigDecimal currentCost = safe(currentEnergy.cost());
        BigDecimal previousCost = safe(previousEnergy.cost());

        return DashboardSummaryResponse.builder()
                .devicesConnected(devicesConnected)
                .activeDevices(activeDevices)
                .roomsMonitored(roomsMonitored)
                .consumedWh(safe(currentEnergy.consumedWh()))
                .consumedKwh(safe(currentEnergy.consumedKwh()))
                .estimatedMonthlyCost(currentCost)
                .costDifferenceFromLastMonth(currentCost.subtract(previousCost))
                .build();
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}