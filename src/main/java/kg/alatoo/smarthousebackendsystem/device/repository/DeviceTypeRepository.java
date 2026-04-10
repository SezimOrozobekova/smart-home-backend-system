package kg.alatoo.smarthousebackendsystem.device.repository;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeviceTypeRepository extends JpaRepository<DeviceType, UUID> {
    Optional<DeviceType> findByCode(String code);
    boolean existsByCode(String code);
}