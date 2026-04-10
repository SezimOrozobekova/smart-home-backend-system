package kg.alatoo.smarthousebackendsystem.device.repository;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeviceStateRepository extends JpaRepository<DeviceState, UUID> {
    Optional<DeviceState> findByDeviceId(UUID deviceId);
}