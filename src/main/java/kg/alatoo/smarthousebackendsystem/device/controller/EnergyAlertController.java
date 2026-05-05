package kg.alatoo.smarthousebackendsystem.device.controller;

import kg.alatoo.smarthousebackendsystem.device.service.EnergyAnomalyService;
import kg.alatoo.smarthousebackendsystem.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/energy-alerts")
@RequiredArgsConstructor
public class EnergyAlertController {

    private final EnergyAnomalyService energyAnomalyService;

    @PostMapping("/check-now")
    public void checkNow(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        energyAnomalyService.checkCurrentUser(currentUser.getId());
    }
}