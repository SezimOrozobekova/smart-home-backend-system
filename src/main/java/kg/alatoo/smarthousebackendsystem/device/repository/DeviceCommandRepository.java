package kg.alatoo.smarthousebackendsystem.device.repository;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceCommand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeviceCommandRepository extends JpaRepository<DeviceCommand, UUID> {
    List<DeviceCommand> findAllByDeviceIdOrderByIssuedAtDesc(UUID deviceId);
}