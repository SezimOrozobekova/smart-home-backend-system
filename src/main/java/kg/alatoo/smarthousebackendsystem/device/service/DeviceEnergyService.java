package kg.alatoo.smarthousebackendsystem.device.service;

import kg.alatoo.smarthousebackendsystem.device.entity.*;
import kg.alatoo.smarthousebackendsystem.device.payload.response.EnergyPointResponse;
import kg.alatoo.smarthousebackendsystem.device.payload.response.MonthlyEnergyResponse;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceConnectionRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceEnergyHistoryRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
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

    private final DeviceRepository deviceRepository;
    private final DeviceConnectionRepository deviceConnectionRepository;
    private final DeviceStateRepository deviceStateRepository;
    private final DeviceEnergyHistoryRepository deviceEnergyHistoryRepository;
    private final ShellyHttpClient shellyHttpClient;

    @Transactional
    @Scheduled(cron = "0 * * * * *") // каждую минуту
    public void collectEnergySnapshots() {
        List<DeviceConnection> connections = deviceConnectionRepository.findAll()
                .stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsEnabled()))
                .filter(c -> c.getConnectionType() == DeviceConnectionType.LOCAL_HTTP)
                .toList();

        for (DeviceConnection connection : connections) {
            try {
                collectForConnection(connection);
            } catch (Exception e) {
                // можно потом заменить на logger.warn(...)
                System.err.println("Failed to collect energy for device: " + connection.getDevice().getId());
            }
        }
    }

    @Transactional
    public void collectForDevice(UUID deviceId) {
        DeviceConnection connection = deviceConnectionRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device connection not found"));

        collectForConnection(connection);
    }

    @Transactional
    protected void collectForConnection(DeviceConnection connection) {
        if (connection.getIpAddress() == null || connection.getIpAddress().isBlank()) {
            return;
        }

        ShellyStatusSnapshot snapshot = shellyHttpClient.getStatus(connection.getIpAddress());
        Device device = connection.getDevice();

        saveHistory(device, snapshot);
        updateDeviceState(device, snapshot);
    }

    private void saveHistory(Device device, ShellyStatusSnapshot snapshot) {
        DeviceEnergyHistory history = new DeviceEnergyHistory();
        history.setDevice(device);
        history.setRecordedAt(Instant.now());
        history.setPowerWatts(snapshot.powerWatts());
        history.setVoltage(snapshot.voltage());
        history.setCurrent(snapshot.current());
        history.setTotalEnergyWh(snapshot.totalEnergyWh());
        history.setTemperatureC(snapshot.temperatureC());

        deviceEnergyHistoryRepository.save(history);
    }

    private void updateDeviceState(Device device, ShellyStatusSnapshot snapshot) {
        DeviceState state = deviceStateRepository.findByDeviceId(device.getId())
                .orElseGet(() -> {
                    DeviceState newState = new DeviceState();
                    newState.setDevice(device);
                    return newState;
                });

        state.setIsOn(snapshot.isOn());
        state.setIsOnline(true);
        state.setPowerWatts(snapshot.powerWatts());
        state.setLastSeenAt(Instant.now());

        deviceStateRepository.save(state);
    }

    public List<EnergyPointResponse> getDailyChart(UUID deviceId, LocalDate date, ZoneId zoneId) {
        Instant from = date.atStartOfDay(zoneId).toInstant();
        Instant to = date.plusDays(1).atStartOfDay(zoneId).toInstant();

        return deviceEnergyHistoryRepository
                .findByDeviceIdAndRecordedAtBetweenOrderByRecordedAtAsc(deviceId, from, to)
                .stream()
                .map(h -> new EnergyPointResponse(
                        h.getRecordedAt(),
                        h.getPowerWatts(),
                        h.getTotalEnergyWh()
                ))
                .toList();
    }

    public MonthlyEnergyResponse getMonthlyConsumption(UUID deviceId, YearMonth month, ZoneId zoneId) {
        Instant from = month.atDay(1).atStartOfDay(zoneId).toInstant();
        Instant to = month.plusMonths(1)
                .atDay(1)
                .atStartOfDay(zoneId)
                .minusNanos(1)
                .toInstant();
        DeviceEnergyHistory first = deviceEnergyHistoryRepository
                .findFirstByDeviceIdAndRecordedAtGreaterThanEqualOrderByRecordedAtAsc(deviceId, from)
                .orElseThrow(() -> new RuntimeException("No energy data found for month start"));

        DeviceEnergyHistory last = deviceEnergyHistoryRepository
                .findFirstByDeviceIdAndRecordedAtLessThanEqualOrderByRecordedAtDesc(deviceId, to)
                .orElseThrow(() -> new RuntimeException("No energy data found for month end"));

        BigDecimal firstTotal = defaultZero(first.getTotalEnergyWh());
        BigDecimal lastTotal = defaultZero(last.getTotalEnergyWh());

        BigDecimal consumedWh = lastTotal.subtract(firstTotal).max(BigDecimal.ZERO);
        BigDecimal consumedKwh = consumedWh.divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP);

        return new MonthlyEnergyResponse(
                month.toString(),
                firstTotal,
                lastTotal,
                consumedWh,
                consumedKwh
        );
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}