package kg.alatoo.smarthousebackendsystem.user.repository;

import kg.alatoo.smarthousebackendsystem.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.role
            """)
    List<User> findAllWithRole();
}
