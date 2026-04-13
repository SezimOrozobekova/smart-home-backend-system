package kg.alatoo.smarthousebackendsystem.home.controller;

import jakarta.validation.Valid;
import kg.alatoo.smarthousebackendsystem.home.payload.request.CreateHomeRequest;
import kg.alatoo.smarthousebackendsystem.home.payload.response.HomeResponse;
import kg.alatoo.smarthousebackendsystem.home.payload.response.RoomDevicesResponse;
import kg.alatoo.smarthousebackendsystem.home.service.HomeService;
import kg.alatoo.smarthousebackendsystem.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/homes")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/my")
    public List<HomeResponse> getMyHomes(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return homeService.getHomesByOwner(currentUser.getId());
    }

    @GetMapping
    public List<HomeResponse> getAllHomes() {
        return homeService.getAllHomes();
    }

    @GetMapping("/owner/{ownerId}")
    public List<HomeResponse> getHomesByOwner(@PathVariable UUID ownerId) {
        return homeService.getHomesByOwner(ownerId);
    }

    @PostMapping
    public HomeResponse createHome(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CreateHomeRequest request
    ) {
        return homeService.createHome(currentUser.getId(), request);
    }

    @GetMapping("/devices-by-room")
    public List<RoomDevicesResponse> getDevicesByRoom(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return homeService.getDevicesByRoom(currentUser.getId());
    }
}