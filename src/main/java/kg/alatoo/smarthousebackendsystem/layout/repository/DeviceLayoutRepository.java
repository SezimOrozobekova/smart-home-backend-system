package kg.alatoo.smarthousebackendsystem.layout.repository;

import kg.alatoo.smarthousebackendsystem.layout.entity.DeviceLayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceLayoutRepository extends JpaRepository<DeviceLayout, UUID> {

    List<DeviceLayout> findAllByDeviceRoomId(UUID roomId);

    void deleteAllByDeviceRoomId(UUID roomId);

    Optional<DeviceLayout> findByDeviceId(UUID deviceId);

    @Modifying
    @Query("""
        delete from DeviceLayout dl
        where dl.device.id = :deviceId
    """)
    void deleteByDeviceId(@Param("deviceId") UUID deviceId);
}