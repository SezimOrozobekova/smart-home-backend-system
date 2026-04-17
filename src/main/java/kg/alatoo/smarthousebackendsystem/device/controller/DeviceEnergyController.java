//package kg.alatoo.smarthousebackendsystem.device.controller;
//
//import kg.alatoo.smarthousebackendsystem.device.payload.response.EnergyPointResponse;
//import kg.alatoo.smarthousebackendsystem.device.payload.response.MonthlyEnergyResponse;
//import kg.alatoo.smarthousebackendsystem.device.service.DeviceEnergyService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.time.YearMonth;
//import java.time.ZoneId;
//import java.util.List;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/device-energy")
//@RequiredArgsConstructor
//public class DeviceEnergyController {
//
//    private final DeviceEnergyService deviceEnergyService;
//
//    @PostMapping("/{deviceId}/collect")
//    public void collectNow(@PathVariable UUID deviceId) {
//        deviceEnergyService.collectForDevice(deviceId);
//    }
//
//    @GetMapping("/{deviceId}/daily-chart")
//    public List<EnergyPointResponse> getDailyChart(
//            @PathVariable UUID deviceId,
//            @RequestParam LocalDate date
//    ) {
//        return deviceEnergyService.getDailyChart(deviceId, date, ZoneId.systemDefault());
//    }
//
//    @GetMapping("/{deviceId}/monthly")
//    public MonthlyEnergyResponse getMonthlyConsumption(
//            @PathVariable UUID deviceId,
//            @RequestParam String month
//    ) {
//        return deviceEnergyService.getMonthlyConsumption(
//                deviceId,
//                YearMonth.parse(month),
//                ZoneId.systemDefault()
//        );
//    }
//}