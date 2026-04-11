package kg.alatoo.smarthousebackendsystem.device.controller;

import kg.alatoo.smarthousebackendsystem.device.payload.request.UpdateDeviceStateRequest;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceStateResponse;
import kg.alatoo.smarthousebackendsystem.device.service.DeviceStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/device-states")
@RequiredArgsConstructor
public class DeviceStateController {

    private final DeviceStateService deviceStateService;

    @GetMapping("/device/{deviceId}")
    public DeviceStateResponse getByDeviceId(@PathVariable UUID deviceId) {
        return deviceStateService.getByDeviceId(deviceId);
    }

    @PatchMapping("/device/{deviceId}")
    public DeviceStateResponse updateByDeviceId(
            @PathVariable UUID deviceId,
            @RequestBody UpdateDeviceStateRequest request
    ) {
        return deviceStateService.updateByDeviceId(deviceId, request);
    }

    @PostMapping("/{deviceId}/toggle")
    public DeviceStateResponse toggleDevice(@PathVariable UUID deviceId) {
        return deviceStateService.toggleDevice(deviceId);
    }
}