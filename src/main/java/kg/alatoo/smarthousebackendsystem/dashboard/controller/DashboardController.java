package kg.alatoo.smarthousebackendsystem.dashboard.controller;

import kg.alatoo.smarthousebackendsystem.dashboard.payload.response.DashboardSummaryResponse;
import kg.alatoo.smarthousebackendsystem.dashboard.service.DashboardService;
import kg.alatoo.smarthousebackendsystem.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return dashboardService.getSummary(currentUser.getId());
    }
}