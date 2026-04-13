package kg.alatoo.smarthousebackendsystem.device.repository;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DeviceStateRepository extends JpaRepository<DeviceState, UUID> {

    Optional<DeviceState> findByDeviceId(UUID deviceId);

    @Modifying
    @Query("""
        delete from DeviceState ds
        where ds.device.room.id = :roomId
    """)
    void deleteAllByDeviceRoomId(@Param("roomId") UUID roomId);

    @Modifying
    @Query("""
        delete from DeviceState ds
        where ds.device.id = :deviceId
    """)
    void deleteByDeviceId(@Param("deviceId") UUID deviceId);
}