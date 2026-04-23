package kg.alatoo.smarthousebackendsystem.device.service.telemetry.cache;

import kg.alatoo.smarthousebackendsystem.device.entity.ShellyStatusSnapshot;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DeviceTelemetryCache {

    private final Map<UUID, ShellyStatusSnapshot> cache = new ConcurrentHashMap<>();

    public void put(UUID deviceId, ShellyStatusSnapshot snapshot) {
        cache.put(deviceId, snapshot);
    }

    public ShellyStatusSnapshot get(UUID deviceId) {
        return cache.get(deviceId);
    }

    public Map<UUID, ShellyStatusSnapshot> getAll() {
        return cache;
    }
}