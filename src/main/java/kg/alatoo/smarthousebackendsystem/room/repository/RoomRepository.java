package kg.alatoo.smarthousebackendsystem.room.repository;

import kg.alatoo.smarthousebackendsystem.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    List<Room> findAllByHomeId(UUID homeId);

    Optional<Room> findByNameAndHomeId(String name, UUID homeId);

    List<Room> findAllByHomeIdIn(Collection<UUID> homeIds);

    Optional<Room> findByIdAndHomeOwnerId(UUID roomId, UUID ownerId);

    List<Room> findAllByHomeOwnerId(UUID ownerId);
}