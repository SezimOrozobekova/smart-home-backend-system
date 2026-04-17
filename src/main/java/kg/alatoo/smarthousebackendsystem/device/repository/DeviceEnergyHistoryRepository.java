package kg.alatoo.smarthousebackendsystem.device.repository;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceEnergyHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceEnergyHistoryRepository extends JpaRepository<DeviceEnergyHistory, UUID> {

    List<DeviceEnergyHistory> findByDeviceIdAndRecordedAtBetweenOrderByRecordedAtAsc(
            UUID deviceId,
            Instant from,
            Instant to
    );

    Optional<DeviceEnergyHistory> findFirstByDeviceIdAndRecordedAtGreaterThanEqualOrderByRecordedAtAsc(
            UUID deviceId,
            Instant from
    );

    Optional<DeviceEnergyHistory> findFirstByDeviceIdAndRecordedAtLessThanEqualOrderByRecordedAtDesc(
            UUID deviceId,
            Instant to
    );

    Optional<DeviceEnergyHistory> findFirstByDeviceIdOrderByRecordedAtDesc(UUID deviceId);

    Optional<DeviceEnergyHistory> findFirstByDeviceRoomHomeOwnerIdAndRecordedAtGreaterThanEqualOrderByRecordedAtAsc(
            UUID userId,
            Instant from
    );

    Optional<DeviceEnergyHistory> findFirstByDeviceRoomHomeOwnerIdAndRecordedAtLessThanOrderByRecordedAtDesc(
            UUID userId,
            Instant to
    );
}