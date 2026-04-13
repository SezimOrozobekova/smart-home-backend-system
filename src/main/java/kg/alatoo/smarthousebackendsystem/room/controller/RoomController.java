package kg.alatoo.smarthousebackendsystem.room.controller;

import jakarta.validation.Valid;
import kg.alatoo.smarthousebackendsystem.room.payload.request.CreateRoomRequest;
import kg.alatoo.smarthousebackendsystem.room.payload.response.RoomResponse;
import kg.alatoo.smarthousebackendsystem.room.service.RoomService;
import kg.alatoo.smarthousebackendsystem.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public List<RoomResponse> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/home/{homeId}")
    public List<RoomResponse> getRoomsByHome(@PathVariable UUID homeId) {
        return roomService.getRoomsByHome(homeId);
    }

    @GetMapping("/my")
    public List<RoomResponse> getMyRooms(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return roomService.getRoomsByUser(currentUser.getId());
    }

    @PostMapping
    public RoomResponse createRoom(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CreateRoomRequest request
    ) {
        return roomService.createRoom(currentUser.getId(), request);
    }

    @DeleteMapping("/{roomId}")
    public void deleteRoom(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID roomId
    ) {
        roomService.deleteRoom(currentUser.getId(), roomId);
    }
}