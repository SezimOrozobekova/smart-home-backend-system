package kg.alatoo.smarthousebackendsystem.device.service;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceEnergyHistory;
import kg.alatoo.smarthousebackendsystem.device.payload.response.EnergyPointResponse;
import kg.alatoo.smarthousebackendsystem.device.payload.response.MonthlyEnergyResponse;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceEnergyHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceEnergyService {

    private final DeviceEnergyHistoryRepository deviceEnergyHistoryRepository;

    public List<EnergyPointResponse> getDailyChart(UUID deviceId, LocalDate date, ZoneId zone) {
        Instant from = date.atStartOfDay(zone).toInstant();
        Instant to = date.plusDays(1).atStartOfDay(zone).toInstant();

        return deviceEnergyHistoryRepository
                .findByDeviceIdAndRecordedAtGreaterThanEqualAndRecordedAtLessThanOrderByRecordedAtAsc(
                        deviceId,
                        from,
                        to
                )
                .stream()
                .map(history -> new EnergyPointResponse(
                        history.getRecordedAt(),
                        history.getPowerWatts(),
                        history.getTotalEnergyWh()
                ))
                .toList();
    }

    public MonthlyEnergyResponse getMonthlyConsumption(UUID deviceId, YearMonth month, ZoneId zone) {
        Instant from = month.atDay(1).atStartOfDay(zone).toInstant();
        Instant to = month.plusMonths(1).atDay(1).atStartOfDay(zone).toInstant();

        BigDecimal consumedWh = safe(
                deviceEnergyHistoryRepository.calculateConsumptionWhByDeviceId(deviceId, from, to)
        );

        BigDecimal consumedKwh = toKwh(consumedWh);

        List<DeviceEnergyHistory> points = deviceEnergyHistoryRepository
                .findByDeviceIdAndRecordedAtGreaterThanEqualAndRecordedAtLessThanOrderByRecordedAtAsc(
                        deviceId,
                        from,
                        to
                );

        BigDecimal firstTotalWh = points.stream()
                .map(DeviceEnergyHistory::getTotalEnergyWh)
                .filter(value -> value != null)
                .findFirst()
                .orElse(BigDecimal.ZERO);

        BigDecimal lastTotalWh = points.stream()
                .map(DeviceEnergyHistory::getTotalEnergyWh)
                .filter(value -> value != null)
                .reduce((first, second) -> second)
                .orElse(BigDecimal.ZERO);

        return new MonthlyEnergyResponse(
                month.toString(),
                firstTotalWh,
                lastTotalWh,
                consumedWh,
                consumedKwh
        );
    }

    public MonthlyEnergyResponse getMonthlyConsumptionByUser(UUID userId, YearMonth month, ZoneId zone) {
        Instant from = month.atDay(1).atStartOfDay(zone).toInstant();
        Instant to = month.plusMonths(1).atDay(1).atStartOfDay(zone).toInstant();

        BigDecimal consumedWh = safe(
                deviceEnergyHistoryRepository.calculateConsumptionWhByUserId(userId, from, to)
        );

        return new MonthlyEnergyResponse(
                month.toString(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                consumedWh,
                toKwh(consumedWh)
        );
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private BigDecimal toKwh(BigDecimal wh) {
        return safe(wh)
                .divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP);
    }
}