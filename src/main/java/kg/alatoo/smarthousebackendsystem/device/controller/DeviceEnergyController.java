package kg.alatoo.smarthousebackendsystem.device.controller;

import kg.alatoo.smarthousebackendsystem.device.payload.response.EnergyPointResponse;
import kg.alatoo.smarthousebackendsystem.device.payload.response.MonthlyEnergyResponse;
import kg.alatoo.smarthousebackendsystem.device.service.DeviceEnergyService;
import kg.alatoo.smarthousebackendsystem.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import kg.alatoo.smarthousebackendsystem.device.payload.request.EnergyPeriod;
import kg.alatoo.smarthousebackendsystem.device.payload.response.EnergyChartPointResponse;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/device-energy")
@RequiredArgsConstructor
public class DeviceEnergyController {

    private final DeviceEnergyService deviceEnergyService;

    @GetMapping("/{deviceId}/daily-chart")
    public List<EnergyPointResponse> getDailyChart(
            @PathVariable UUID deviceId,
            @RequestParam LocalDate date
    ) {
        return deviceEnergyService.getDailyChart(
                deviceId,
                date,
                ZoneId.systemDefault()
        );
    }

    @GetMapping("/{deviceId}/monthly")
    public MonthlyEnergyResponse getMonthlyConsumption(
            @PathVariable UUID deviceId,
            @RequestParam String month
    ) {
        return deviceEnergyService.getMonthlyConsumption(
                deviceId,
                YearMonth.parse(month),
                ZoneId.systemDefault()
        );
    }

    @GetMapping("/monthly")
    public MonthlyEnergyResponse getMyMonthlyConsumption(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam String month
    ) {
        return deviceEnergyService.getMonthlyConsumptionByUser(
                currentUser.getId(),
                YearMonth.parse(month),
                ZoneId.systemDefault()
        );
    }

    @GetMapping("/chart")
    public List<EnergyChartPointResponse> getMyEnergyChart(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam EnergyPeriod period,
            @RequestParam LocalDate date
    ) {
        return deviceEnergyService.getEnergyChartByUser(
                currentUser.getId(),
                period,
                date,
                ZoneId.systemDefault()
        );
    }
}