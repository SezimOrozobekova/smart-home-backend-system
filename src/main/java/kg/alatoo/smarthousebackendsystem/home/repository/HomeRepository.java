package kg.alatoo.smarthousebackendsystem.home.repository;

import kg.alatoo.smarthousebackendsystem.home.entity.Home;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HomeRepository extends JpaRepository<Home, UUID> {
    List<Home> findAllByOwnerId(UUID ownerId);
    Optional<Home> findByNameAndOwnerId(String name, UUID ownerId);
    Optional<Home> findByIdAndOwnerId(UUID id, UUID ownerId);
}