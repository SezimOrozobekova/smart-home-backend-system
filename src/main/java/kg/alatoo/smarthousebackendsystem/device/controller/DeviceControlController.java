package kg.alatoo.smarthousebackendsystem.device.controller;

import jakarta.validation.Valid;
import kg.alatoo.smarthousebackendsystem.device.payload.request.SetDevicePowerRequest;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceStateResponse;
import kg.alatoo.smarthousebackendsystem.device.service.control.DeviceControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/device-states")
@RequiredArgsConstructor
public class DeviceControlController {

    private final DeviceControlService deviceControlService;

    @PostMapping("/{deviceId}/power")
    public DeviceStateResponse setPower(
            @PathVariable UUID deviceId,
            @Valid @RequestBody SetDevicePowerRequest request
    ) {
        return deviceControlService.setDevicePower(deviceId, request.on());
    }
}