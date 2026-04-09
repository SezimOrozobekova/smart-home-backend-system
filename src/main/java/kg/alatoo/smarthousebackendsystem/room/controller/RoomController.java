package kg.alatoo.smarthousebackendsystem.room.controller;

import jakarta.validation.Valid;
import kg.alatoo.smarthousebackendsystem.room.payload.request.CreateRoomRequest;
import kg.alatoo.smarthousebackendsystem.room.payload.response.RoomResponse;
import kg.alatoo.smarthousebackendsystem.room.service.RoomService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public RoomResponse createRoom(@Valid @RequestBody CreateRoomRequest request) {
        return roomService.createRoom(request);
    }
}