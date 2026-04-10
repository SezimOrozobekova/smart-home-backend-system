package kg.alatoo.smarthousebackendsystem.device.controller;

import jakarta.validation.Valid;
import kg.alatoo.smarthousebackendsystem.device.payload.request.CreateDeviceTypeRequest;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceTypeResponse;
import kg.alatoo.smarthousebackendsystem.device.service.DeviceTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device-types")
@RequiredArgsConstructor
public class DeviceTypeController {

    private final DeviceTypeService deviceTypeService;

    @GetMapping
    public List<DeviceTypeResponse> getAll() {
        return deviceTypeService.getAll();
    }

    @PostMapping
    public DeviceTypeResponse create(@Valid @RequestBody CreateDeviceTypeRequest request) {
        return deviceTypeService.create(request);
    }
}