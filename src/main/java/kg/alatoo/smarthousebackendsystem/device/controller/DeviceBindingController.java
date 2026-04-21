package kg.alatoo.smarthousebackendsystem.device.controller;

import jakarta.validation.Valid;
import kg.alatoo.smarthousebackendsystem.device.payload.request.BindConfirmRequest;
import kg.alatoo.smarthousebackendsystem.device.payload.request.BindInitRequest;
import kg.alatoo.smarthousebackendsystem.device.payload.response.BindInitResponse;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceConnectionResponse;
import kg.alatoo.smarthousebackendsystem.device.service.control.DeviceBindingService;
import kg.alatoo.smarthousebackendsystem.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/devices/{deviceId}/binding")
@RequiredArgsConstructor
public class DeviceBindingController {

    private final DeviceBindingService deviceBindingService;

    @PostMapping("/init")
    public BindInitResponse initBind(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID deviceId,
            @Valid @RequestBody BindInitRequest request
    ) {
        return deviceBindingService.initBind(currentUser.getId(), deviceId, request);
    }

    @PostMapping("/confirm")
    public DeviceConnectionResponse confirmBind(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID deviceId,
            @Valid @RequestBody BindConfirmRequest request
    ) {
        return deviceBindingService.confirmBind(currentUser.getId(), deviceId, request);
    }
}