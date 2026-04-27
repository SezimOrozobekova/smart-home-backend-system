package kg.alatoo.smarthousebackendsystem.device.repository;

import kg.alatoo.smarthousebackendsystem.device.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {
    List<Device> findAllByRoomId(UUID roomId);
    Optional<Device> findByExternalId(String externalId);
    void deleteAllByRoomId(UUID roomId);
    List<Device> findAllByRoomHomeOwnerId(UUID userId);
    Optional<Device> findByIdAndRoomHomeOwnerId(UUID deviceId, UUID userId);
    long countByRoomHomeOwnerId(UUID userId);
    Optional<Device> findByIdAndRoomId(UUID deviceId, UUID roomId);
}