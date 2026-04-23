package kg.alatoo.smarthousebackendsystem.device.service.telemetry.scheduler;

import kg.alatoo.smarthousebackendsystem.device.entity.Device;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceEnergyHistory;
import kg.alatoo.smarthousebackendsystem.device.entity.ShellyStatusSnapshot;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceEnergyHistoryRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceRepository;
import kg.alatoo.smarthousebackendsystem.device.service.telemetry.cache.DeviceTelemetryCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceTelemetrySnapshotScheduler {

    private final DeviceTelemetryCache deviceTelemetryCache;
    private final DeviceRepository deviceRepository;
    private final DeviceEnergyHistoryRepository deviceEnergyHistoryRepository;

    @Transactional
    @Scheduled(fixedRate = 300000) // 5 минут
    public void saveSnapshots() {
        Map<UUID, ShellyStatusSnapshot> cache = deviceTelemetryCache.getAll();

        for (Map.Entry<UUID, ShellyStatusSnapshot> entry : cache.entrySet()) {
            UUID deviceId = entry.getKey();
            ShellyStatusSnapshot snapshot = entry.getValue();

            Device device = deviceRepository.findById(deviceId).orElse(null);
            if (device == null) {
                continue;
            }

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

        log.info("Saved telemetry snapshots for {} devices", cache.size());
    }
}