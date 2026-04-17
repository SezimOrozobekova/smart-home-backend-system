package kg.alatoo.smarthousebackendsystem.device.repository;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceEnergyHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
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

    @Query(value = """
        WITH first_readings AS (
            SELECT DISTINCT ON (deh.device_id)
                deh.device_id,
                deh.total_energy_wh AS first_total
            FROM device_energy_history deh
            JOIN devices d ON d.id = deh.device_id
            JOIN rooms r ON r.id = d.room_id
            JOIN homes h ON h.id = r.home_id
            WHERE h.owner_id = :userId
              AND deh.recorded_at >= :from
              AND deh.recorded_at <= :to
            ORDER BY deh.device_id, deh.recorded_at ASC
        ),
        last_readings AS (
            SELECT DISTINCT ON (deh.device_id)
                deh.device_id,
                deh.total_energy_wh AS last_total
            FROM device_energy_history deh
            JOIN devices d ON d.id = deh.device_id
            JOIN rooms r ON r.id = d.room_id
            JOIN homes h ON h.id = r.home_id
            WHERE h.owner_id = :userId
              AND deh.recorded_at >= :from
              AND deh.recorded_at <= :to
            ORDER BY deh.device_id, deh.recorded_at DESC
        )
        SELECT COALESCE(SUM(GREATEST(l.last_total - f.first_total, 0)), 0)
        FROM first_readings f
        JOIN last_readings l ON l.device_id = f.device_id
        """, nativeQuery = true)
    BigDecimal calculateMonthlyConsumptionWhByUserId(
            UUID userId,
            Instant from,
            Instant to
    );
}