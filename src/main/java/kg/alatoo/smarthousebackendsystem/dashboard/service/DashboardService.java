package kg.alatoo.smarthousebackendsystem.dashboard.service;

import kg.alatoo.smarthousebackendsystem.dashboard.payload.response.DashboardSummaryResponse;
import kg.alatoo.smarthousebackendsystem.device.entity.Device;
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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final BigDecimal PRICE_PER_KWH = new BigDecimal("0.15");

    private final DeviceRepository deviceRepository;
    private final DeviceStateRepository deviceStateRepository;
    private final RoomRepository roomRepository;
    private final DeviceEnergyService deviceEnergyService;

    public DashboardSummaryResponse getSummary(UUID userId) {

        // 1. Простая статистика
        long devicesConnected = deviceRepository.countByRoomHomeOwnerId(userId);
        long activeDevices = deviceStateRepository.countByDeviceRoomHomeOwnerIdAndIsOnTrue(userId);
        long roomsMonitored = roomRepository.countByHomeOwnerId(userId);

        // 2. Берём все устройства пользователя
        List<Device> devices = deviceRepository.findAllByRoomHomeOwnerId(userId);

        YearMonth currentMonth = YearMonth.now();
        YearMonth previousMonth = currentMonth.minusMonths(1);
        ZoneId zone = ZoneId.systemDefault();

        BigDecimal currentTotalKwh = BigDecimal.ZERO;
        BigDecimal previousTotalKwh = BigDecimal.ZERO;

        // 3. Считаем потребление
        for (Device device : devices) {

            // текущий месяц
            try {
                MonthlyEnergyResponse current =
                        deviceEnergyService.getMonthlyConsumption(device.getId(), currentMonth, zone);

                currentTotalKwh = currentTotalKwh.add(current.consumedKwh());
            } catch (Exception ignored) {}

            // прошлый месяц
            try {
                MonthlyEnergyResponse previous =
                        deviceEnergyService.getMonthlyConsumption(device.getId(), previousMonth, zone);

                previousTotalKwh = previousTotalKwh.add(previous.consumedKwh());
            } catch (Exception ignored) {}
        }

        // 4. Переводим в деньги
        BigDecimal currentCost = currentTotalKwh.multiply(PRICE_PER_KWH);
        BigDecimal previousCost = previousTotalKwh.multiply(PRICE_PER_KWH);

        return DashboardSummaryResponse.builder()
                .devicesConnected(devicesConnected)
                .activeDevices(activeDevices)
                .roomsMonitored(roomsMonitored)
                .estimatedMonthlyCost(currentCost.doubleValue())
                .costDifferenceFromLastMonth(
                        currentCost.subtract(previousCost).doubleValue()
                )
                .build();
    }
}