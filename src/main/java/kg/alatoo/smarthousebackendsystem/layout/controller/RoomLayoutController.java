package kg.alatoo.smarthousebackendsystem.layout.controller;

import jakarta.validation.Valid;
import kg.alatoo.smarthousebackendsystem.layout.payload.request.SaveRoomLayoutRequest;
import kg.alatoo.smarthousebackendsystem.layout.payload.response.RoomLayoutResponse;
import kg.alatoo.smarthousebackendsystem.layout.service.RoomLayoutService;
import kg.alatoo.smarthousebackendsystem.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomLayoutController {

    private final RoomLayoutService roomLayoutService;

    @GetMapping("/layouts/my")
    public List<RoomLayoutResponse> getMyRoomLayouts(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return roomLayoutService.getMyRoomLayouts(currentUser.getId());
    }

    @PutMapping("/{roomId}/layout")
    public void saveLayout(
            @PathVariable UUID roomId,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody SaveRoomLayoutRequest request
    ) {
        roomLayoutService.saveRoomLayout(currentUser.getId(), roomId, request);
    }

    @GetMapping("/{roomId}/layout")
    public RoomLayoutResponse getLayout(
            @PathVariable UUID roomId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return roomLayoutService.getRoomLayout(currentUser.getId(), roomId);
    }
}