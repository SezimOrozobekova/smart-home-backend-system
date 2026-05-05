package kg.alatoo.smarthousebackendsystem.device.repository;

import kg.alatoo.smarthousebackendsystem.device.entity.EnergyAlert;
import kg.alatoo.smarthousebackendsystem.device.entity.EnergyAlertType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.UUID;

public interface EnergyAlertRepository extends JpaRepository<EnergyAlert, UUID> {

    boolean existsByUserIdAndAlertTypeAndAlertDate(
            UUID userId,
            EnergyAlertType alertType,
            LocalDate alertDate
    );
}