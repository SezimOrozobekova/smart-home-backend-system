package kg.alatoo.smarthousebackendsystem.room.service;

import kg.alatoo.smarthousebackendsystem.device.repository.DeviceRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceStateRepository;
import kg.alatoo.smarthousebackendsystem.home.entity.Home;
import kg.alatoo.smarthousebackendsystem.home.repository.HomeRepository;
import kg.alatoo.smarthousebackendsystem.layout.repository.DeviceLayoutRepository;
import kg.alatoo.smarthousebackendsystem.room.entity.Room;
import kg.alatoo.smarthousebackendsystem.room.mapper.RoomMapper;
import kg.alatoo.smarthousebackendsystem.room.payload.request.CreateRoomRequest;
import kg.alatoo.smarthousebackendsystem.room.payload.response.RoomResponse;
import kg.alatoo.smarthousebackendsystem.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;
    private final HomeRepository homeRepository;
    private final RoomMapper roomMapper;
    private final DeviceRepository deviceRepository;
    private final DeviceLayoutRepository deviceLayoutRepository;
    private final DeviceStateRepository deviceStateRepository;

    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    public List<RoomResponse> getRoomsByHome(UUID homeId) {
        return roomRepository.findAllByHomeId(homeId)
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @Transactional
    public RoomResponse createRoom(UUID userId, CreateRoomRequest request) {
        Home home = homeRepository.findByIdAndOwnerId(request.homeId(), userId)
                .orElseThrow(() -> new RuntimeException("Home not found or access denied"));

        Room room = new Room();
        room.setName(request.name());
        room.setHome(home);

        Room savedRoom = roomRepository.save(room);

        return roomMapper.toResponse(savedRoom);
    }

    public List<RoomResponse> getRoomsByUser(UUID userId) {
        List<Home> homes = homeRepository.findAllByOwnerId(userId);

        if (homes.isEmpty()) {
            return List.of();
        }

        List<UUID> homeIds = homes.stream()
                .map(Home::getId)
                .toList();

        List<Room> rooms = roomRepository.findAllByHomeIdIn(homeIds);

        return rooms.stream()
                .map(room -> new RoomResponse(
                        room.getId(),
                        room.getName(),
                        room.getHome().getId(),
                        room.getCreatedAt(),
                        room.getUpdatedAt()
                ))
                .toList();
    }

    @Transactional
    public void deleteRoom(UUID userId, UUID roomId) {
        Room room = roomRepository.findByIdAndHomeOwnerId(roomId, userId)
                .orElseThrow(() -> new RuntimeException("Room not found or access denied"));

        deviceStateRepository.deleteAllByDeviceRoomId(roomId);
        deviceLayoutRepository.deleteAllByDeviceRoomId(roomId);
        deviceRepository.deleteAllByRoomId(roomId);
        roomRepository.delete(room);
    }
}