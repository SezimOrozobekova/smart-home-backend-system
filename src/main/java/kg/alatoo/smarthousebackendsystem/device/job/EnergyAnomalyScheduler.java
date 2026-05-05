package kg.alatoo.smarthousebackendsystem.device.job;

import kg.alatoo.smarthousebackendsystem.device.service.EnergyAnomalyService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnergyAnomalyScheduler {

    private final EnergyAnomalyService energyAnomalyService;

    @Scheduled(cron = "0 55 23 * * *")
    public void checkDailyEnergyAnomalies() {
        energyAnomalyService.checkAllUsers();
    }
}