package kg.alatoo.smarthousebackendsystem.device.repository;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceEnergyHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    @Query("""
        select h from DeviceEnergyHistory h
        where h.device.id = :deviceId
        order by h.recordedAt desc
        limit 1
    """)
    Optional<DeviceEnergyHistory> findLatestByDeviceId(UUID deviceId);
}