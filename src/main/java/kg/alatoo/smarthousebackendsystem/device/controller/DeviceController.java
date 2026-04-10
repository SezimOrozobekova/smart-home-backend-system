package kg.alatoo.smarthousebackendsystem.device.controller;

import jakarta.validation.Valid;
import kg.alatoo.smarthousebackendsystem.device.payload.request.CreateDeviceRequest;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceResponse;
import kg.alatoo.smarthousebackendsystem.device.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping("/{id}")
    public DeviceResponse getById(@PathVariable UUID id) {
        return deviceService.getById(id);
    }

    @GetMapping("/room/{roomId}")
    public List<DeviceResponse> getAllByRoom(@PathVariable UUID roomId) {
        return deviceService.getAllByRoom(roomId);
    }

    @PostMapping
    public DeviceResponse create(@Valid @RequestBody CreateDeviceRequest request) {
        return deviceService.create(request);
    }
}