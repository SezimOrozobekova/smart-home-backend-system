package kg.alatoo.smarthousebackendsystem.room.service;

import kg.alatoo.smarthousebackendsystem.home.entity.Home;
import kg.alatoo.smarthousebackendsystem.home.repository.HomeRepository;
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
    public RoomResponse createRoom(CreateRoomRequest request) {
        Home home = homeRepository.findById(request.homeId())
                .orElseThrow(() -> new RuntimeException("Home not found"));

        Room room = new Room();
        room.setName(request.name());
        room.setHome(home);

        Room savedRoom = roomRepository.save(room);

        return roomMapper.toResponse(savedRoom);
    }
}