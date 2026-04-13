package kg.alatoo.smarthousebackendsystem.device.repository;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeviceConnectionRepository extends JpaRepository<DeviceConnection, UUID> {

    Optional<DeviceConnection> findByDeviceId(UUID deviceId);

    Optional<DeviceConnection> findByDeviceIdAndDeviceRoomHomeOwnerId(UUID deviceId, UUID userId);

    void deleteByDeviceId(UUID deviceId);
}