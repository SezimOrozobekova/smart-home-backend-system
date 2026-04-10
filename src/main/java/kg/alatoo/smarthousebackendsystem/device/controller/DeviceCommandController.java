package kg.alatoo.smarthousebackendsystem.device.controller;

import jakarta.validation.Valid;
import kg.alatoo.smarthousebackendsystem.device.payload.request.CreateDeviceCommandRequest;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceCommandResponse;
import kg.alatoo.smarthousebackendsystem.device.service.DeviceCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/device-commands")
@RequiredArgsConstructor
public class DeviceCommandController {

    private final DeviceCommandService deviceCommandService;

    @GetMapping("/device/{deviceId}")
    public List<DeviceCommandResponse> getAllByDeviceId(@PathVariable UUID deviceId) {
        return deviceCommandService.getAllByDeviceId(deviceId);
    }

    @PostMapping
    public DeviceCommandResponse create(@Valid @RequestBody CreateDeviceCommandRequest request) {
        return deviceCommandService.create(request);
    }
}