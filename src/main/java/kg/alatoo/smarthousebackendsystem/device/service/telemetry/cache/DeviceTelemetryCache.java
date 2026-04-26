package kg.alatoo.smarthousebackendsystem.device.service.telemetry.cache;

import kg.alatoo.smarthousebackendsystem.device.entity.ShellyStatusSnapshot;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DeviceTelemetryCache {

    private final Map<UUID, ShellyStatusSnapshot> cache = new ConcurrentHashMap<>();

    public void merge(UUID deviceId, ShellyStatusSnapshot incoming) {
        cache.merge(deviceId, incoming, this::mergeSnapshots);
    }

    public ShellyStatusSnapshot get(UUID deviceId) {
        return cache.get(deviceId);
    }

    public Map<UUID, ShellyStatusSnapshot> getAll() {
        return cache;
    }

    private ShellyStatusSnapshot mergeSnapshots(
            ShellyStatusSnapshot oldSnapshot,
            ShellyStatusSnapshot newSnapshot
    ) {
        return new ShellyStatusSnapshot(
                newSnapshot.isOn() != null
                        ? newSnapshot.isOn()
                        : oldSnapshot.isOn(),

                newSnapshot.powerWatts() != null
                        ? newSnapshot.powerWatts()
                        : oldSnapshot.powerWatts(),

                newSnapshot.voltage() != null
                        ? newSnapshot.voltage()
                        : oldSnapshot.voltage(),

                newSnapshot.current() != null
                        ? newSnapshot.current()
                        : oldSnapshot.current(),

                newSnapshot.totalEnergyWh() != null
                        ? newSnapshot.totalEnergyWh()
                        : oldSnapshot.totalEnergyWh(),

                newSnapshot.temperatureC() != null
                        ? newSnapshot.temperatureC()
                        : oldSnapshot.temperatureC()
        );
    }
}