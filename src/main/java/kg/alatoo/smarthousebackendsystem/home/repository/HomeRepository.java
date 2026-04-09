package kg.alatoo.smarthousebackendsystem.home.repository;

import kg.alatoo.smarthousebackendsystem.home.entity.Home;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HomeRepository extends JpaRepository<Home, UUID> {
    List<Home> findAllByOwnerId(UUID ownerId);
}