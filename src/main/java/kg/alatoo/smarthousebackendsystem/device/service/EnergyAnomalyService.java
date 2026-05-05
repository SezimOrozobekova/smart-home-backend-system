package kg.alatoo.smarthousebackendsystem.device.service;

import kg.alatoo.smarthousebackendsystem.device.entity.EnergyAlert;
import kg.alatoo.smarthousebackendsystem.device.entity.EnergyAlertType;
import kg.alatoo.smarthousebackendsystem.device.repository.EnergyAlertRepository;
import kg.alatoo.smarthousebackendsystem.device.service.email.EmailService;
import kg.alatoo.smarthousebackendsystem.user.entity.User;
import kg.alatoo.smarthousebackendsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnergyAnomalyService {

    private static final BigDecimal MULTIPLIER = BigDecimal.valueOf(1.5);

    private final UserRepository userRepository;
    private final DeviceEnergyService deviceEnergyService;
    private final EnergyAlertRepository energyAlertRepository;
    private final EmailService emailService;

    @Transactional
    public void checkAllUsers() {
        LocalDate today = LocalDate.now();
        ZoneId zone = ZoneId.systemDefault();

        for (User user : userRepository.findAll()) {
            checkUser(user, today, zone);
        }
    }

    @Transactional
    public void checkCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        checkUser(user, LocalDate.now(), ZoneId.systemDefault());
    }

    private void checkUser(User user, LocalDate today, ZoneId zone) {
        BigDecimal todayKwh = deviceEnergyService.getConsumptionKwhByUserForDate(
                user.getId(),
                today,
                zone
        );

        BigDecimal averageKwh = deviceEnergyService.getAverageDailyConsumptionKwh(
                user.getId(),
                today.minusDays(7),
                today.minusDays(1),
                zone
        );

        log.info("Energy anomaly check: user={}, todayKwh={}, averageKwh={}",
                user.getEmail(), todayKwh, averageKwh);

        if (averageKwh.compareTo(BigDecimal.ZERO) == 0) {
            log.info("Skipping anomaly check: no historical data for user={}", user.getEmail());
            return;
        }

        BigDecimal threshold = averageKwh.multiply(MULTIPLIER);

        if (todayKwh.compareTo(threshold) <= 0) {
            log.info("No anomaly detected: todayKwh={} <= threshold={} for user={}",
                    todayKwh, threshold, user.getEmail());
            return;
        }

        boolean alreadyExists = energyAlertRepository.existsByUserIdAndAlertTypeAndAlertDate(
                user.getId(),
                EnergyAlertType.ENERGY_ANOMALY,
                today
        );

        if (alreadyExists) {
            log.info("Energy anomaly alert already exists for user={} date={}",
                    user.getEmail(), today);
            return;
        }

        BigDecimal differencePercent = todayKwh
                .subtract(averageKwh)
                .multiply(BigDecimal.valueOf(100))
                .divide(averageKwh, 2, RoundingMode.HALF_UP);

        EnergyAlert alert = new EnergyAlert();
        alert.setUser(user);
        alert.setAlertType(EnergyAlertType.ENERGY_ANOMALY);
        alert.setMessage("Unusual energy consumption detected");
        alert.setTodayKwh(todayKwh);
        alert.setAverageKwh(averageKwh);
        alert.setDifferencePercent(differencePercent);
        alert.setAlertDate(today);

        energyAlertRepository.save(alert);

        emailService.sendEnergyAnomalyAlert(
                user.getEmail(),
                todayKwh,
                averageKwh,
                differencePercent
        );

        log.info("Energy anomaly email sent to user={}", user.getEmail());
    }
}