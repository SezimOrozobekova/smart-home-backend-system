package kg.alatoo.smarthousebackendsystem.device.repository;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceEnergyHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kg.alatoo.smarthousebackendsystem.device.repository.projection.EnergyChartPointProjection;

public interface DeviceEnergyHistoryRepository extends JpaRepository<DeviceEnergyHistory, UUID> {
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

    List<DeviceEnergyHistory> findByDeviceIdAndRecordedAtGreaterThanEqualAndRecordedAtLessThanOrderByRecordedAtAsc(
            UUID deviceId,
            Instant from,
            Instant to
    );

    @Query(value = """
        WITH first_reading AS (
            SELECT deh.total_energy_wh AS first_total
            FROM device_energy_history deh
            WHERE deh.device_id = :deviceId
              AND deh.recorded_at >= :from
              AND deh.recorded_at < :to
              AND deh.total_energy_wh IS NOT NULL
            ORDER BY deh.recorded_at ASC
            LIMIT 1
        ),
        last_reading AS (
            SELECT deh.total_energy_wh AS last_total
            FROM device_energy_history deh
            WHERE deh.device_id = :deviceId
              AND deh.recorded_at >= :from
              AND deh.recorded_at < :to
              AND deh.total_energy_wh IS NOT NULL
            ORDER BY deh.recorded_at DESC
            LIMIT 1
        )
        SELECT COALESCE(
            GREATEST(
                (SELECT last_total FROM last_reading) - (SELECT first_total FROM first_reading),
                0
            ),
            0
        )
        """, nativeQuery = true)
    BigDecimal calculateConsumptionWhByDeviceId(
            UUID deviceId,
            Instant from,
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
              AND deh.recorded_at < :to
              AND deh.total_energy_wh IS NOT NULL
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
              AND deh.recorded_at < :to
              AND deh.total_energy_wh IS NOT NULL
            ORDER BY deh.device_id, deh.recorded_at DESC
        )
        SELECT COALESCE(SUM(GREATEST(l.last_total - f.first_total, 0)), 0)
        FROM first_readings f
        JOIN last_readings l ON l.device_id = f.device_id
        """, nativeQuery = true)
    BigDecimal calculateConsumptionWhByUserId(
            UUID userId,
            Instant from,
            Instant to
    );

    @Query(value = """
    WITH bucketed AS (
        SELECT
            date_trunc('hour', deh.recorded_at) AS bucket,
            deh.device_id,
            deh.total_energy_wh,
            deh.recorded_at
        FROM device_energy_history deh
        JOIN devices d ON d.id = deh.device_id
        JOIN rooms r ON r.id = d.room_id
        JOIN homes h ON h.id = r.home_id
        WHERE h.owner_id = :userId
          AND deh.recorded_at >= :from
          AND deh.recorded_at < :to
          AND deh.total_energy_wh IS NOT NULL
    ),
    first_readings AS (
        SELECT DISTINCT ON (bucket, device_id)
            bucket,
            device_id,
            total_energy_wh AS first_total
        FROM bucketed
        ORDER BY bucket, device_id, recorded_at ASC
    ),
    last_readings AS (
        SELECT DISTINCT ON (bucket, device_id)
            bucket,
            device_id,
            total_energy_wh AS last_total
        FROM bucketed
        ORDER BY bucket, device_id, recorded_at DESC
    )
    SELECT
        to_char(f.bucket, 'YYYY-MM-DD HH24:00') AS label,
        COALESCE(SUM(GREATEST(l.last_total - f.first_total, 0)), 0) AS consumedWh
    FROM first_readings f
    JOIN last_readings l
      ON l.bucket = f.bucket
     AND l.device_id = f.device_id
    GROUP BY f.bucket
    ORDER BY f.bucket
    """, nativeQuery = true)
    List<EnergyChartPointProjection> getUserHourlyEnergyChart(
            UUID userId,
            Instant from,
            Instant to
    );

    @Query(value = """
    WITH bucketed AS (
        SELECT
            date_trunc('day', deh.recorded_at) AS bucket,
            deh.device_id,
            deh.total_energy_wh,
            deh.recorded_at
        FROM device_energy_history deh
        JOIN devices d ON d.id = deh.device_id
        JOIN rooms r ON r.id = d.room_id
        JOIN homes h ON h.id = r.home_id
        WHERE h.owner_id = :userId
          AND deh.recorded_at >= :from
          AND deh.recorded_at < :to
          AND deh.total_energy_wh IS NOT NULL
    ),
    first_readings AS (
        SELECT DISTINCT ON (bucket, device_id)
            bucket,
            device_id,
            total_energy_wh AS first_total
        FROM bucketed
        ORDER BY bucket, device_id, recorded_at ASC
    ),
    last_readings AS (
        SELECT DISTINCT ON (bucket, device_id)
            bucket,
            device_id,
            total_energy_wh AS last_total
        FROM bucketed
        ORDER BY bucket, device_id, recorded_at DESC
    )
    SELECT
        to_char(f.bucket, 'YYYY-MM-DD') AS label,
        COALESCE(SUM(GREATEST(l.last_total - f.first_total, 0)), 0) AS consumedWh
    FROM first_readings f
    JOIN last_readings l
      ON l.bucket = f.bucket
     AND l.device_id = f.device_id
    GROUP BY f.bucket
    ORDER BY f.bucket
    """, nativeQuery = true)
    List<EnergyChartPointProjection> getUserDailyEnergyChart(
            UUID userId,
            Instant from,
            Instant to
    );
}