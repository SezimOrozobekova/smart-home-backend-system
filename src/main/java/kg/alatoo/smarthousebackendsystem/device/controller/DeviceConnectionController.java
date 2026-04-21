package kg.alatoo.smarthousebackendsystem.device.controller;

import jakarta.validation.Valid;
import kg.alatoo.smarthousebackendsystem.device.payload.request.UpsertDeviceConnectionRequest;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceConnectionResponse;
import kg.alatoo.smarthousebackendsystem.device.service.control.DeviceConnectionService;
import kg.alatoo.smarthousebackendsystem.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/device-connections")
@RequiredArgsConstructor
public class DeviceConnectionController {

    private final DeviceConnectionService deviceConnectionService;

    @GetMapping("/device/{deviceId}")
    public DeviceConnectionResponse getByDeviceId(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID deviceId
    ) {
        return deviceConnectionService.getByDeviceId(currentUser.getId(), deviceId);
    }

    @PutMapping("/device/{deviceId}")
    public DeviceConnectionResponse upsert(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID deviceId,
            @Valid @RequestBody UpsertDeviceConnectionRequest request
    ) {
        return deviceConnectionService.upsert(currentUser.getId(), deviceId, request);
    }

    @DeleteMapping("/device/{deviceId}")
    public void delete(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID deviceId
    ) {
        deviceConnectionService.delete(currentUser.getId(), deviceId);
    }
}