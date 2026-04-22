package kg.alatoo.smarthousebackendsystem.home.service;

import kg.alatoo.smarthousebackendsystem.device.entity.Device;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceState;
import kg.alatoo.smarthousebackendsystem.device.mapper.DeviceMapper;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceStateRepository;
import kg.alatoo.smarthousebackendsystem.home.entity.Home;
import kg.alatoo.smarthousebackendsystem.home.mapper.HomeMapper;
import kg.alatoo.smarthousebackendsystem.home.payload.request.CreateHomeRequest;
import kg.alatoo.smarthousebackendsystem.home.payload.response.DeviceItemResponse;
import kg.alatoo.smarthousebackendsystem.home.payload.response.HomeResponse;
import kg.alatoo.smarthousebackendsystem.home.payload.response.RoomDevicesResponse;
import kg.alatoo.smarthousebackendsystem.home.repository.HomeRepository;
import kg.alatoo.smarthousebackendsystem.room.entity.Room;
import kg.alatoo.smarthousebackendsystem.room.repository.RoomRepository;
import kg.alatoo.smarthousebackendsystem.user.entity.User;
import kg.alatoo.smarthousebackendsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private final HomeRepository homeRepository;
    private final UserRepository userRepository;
    private final HomeMapper homeMapper;
    private final DeviceMapper deviceMapper;
    private final RoomRepository roomRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceStateRepository deviceStateRepository;

    public List<HomeResponse> getAllHomes() {
        return homeRepository.findAll()
                .stream()
                .map(homeMapper::toResponse)
                .toList();
    }

    public List<HomeResponse> getHomesByOwner(UUID ownerId) {
        List<Home> homes = homeRepository.findAllByOwnerId(ownerId);

        return homes.stream()
                .map(homeMapper::toResponse)
                .toList();
    }

    @Transactional
    public HomeResponse createHome(UUID ownerId, CreateHomeRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Home home = homeMapper.toEntity(request);
        home.setOwner(owner);

        Home saved = homeRepository.save(home);

        return homeMapper.toResponse(saved);
    }

    public List<RoomDevicesResponse> getDevicesByRoom(UUID userId) {
        List<Home> homes = homeRepository.findAllByOwnerId(userId);

        if (homes.isEmpty()) {
            return List.of();
        }

        List<UUID> homeIds = homes.stream()
                .map(Home::getId)
                .toList();

        List<Room> rooms = roomRepository.findAllByHomeIdIn(homeIds);

        return rooms.stream()
                .map(room -> {
                    List<Device> devices = deviceRepository.findAllByRoomId(room.getId());

                    List<DeviceItemResponse> deviceResponses = devices.stream()
                            .map(device -> {
                                DeviceState state = deviceStateRepository.findByDeviceId(device.getId())
                                        .orElse(null);

                                Integer power = 0;
                                Integer basePower = 0;
                                Boolean active = false;
                                Boolean online = false;
                                Instant updatedAt = null;
                                Boolean isOn = false;

                                if (state != null) {
                                    isOn = Boolean.TRUE.equals(state.getIsOn());
                                    power = state.getPowerWatts() != null
                                            ? state.getPowerWatts().intValue()
                                            : 0;

                                    basePower = state.getPeakCapacityWatts() != null
                                            ? state.getPeakCapacityWatts().intValue()
                                            : 0;

                                    active = Boolean.TRUE.equals(state.getIsOn());
                                    online = Boolean.TRUE.equals(state.getIsOnline());
                                    updatedAt = state.getLastSeenAt() != null
                                            ? state.getLastSeenAt()
                                            : state.getRecordedAt();
                                }

                                return deviceMapper.toResponse(
                                        device,
                                        room.getName(),
                                        power,
                                        basePower,
                                        active,
                                        online,
                                        updatedAt,
                                        isOn
                                );
                            })
                            .toList();

                    int activeDevices = (int) deviceResponses.stream()
                            .filter(device -> Boolean.TRUE.equals(device.isOn()))
                            .count();

                    int totalPower = deviceResponses.stream()
                            .map(DeviceItemResponse::power)
                            .filter(powerValue -> powerValue != null)
                            .reduce(0, Integer::sum);

                    return new RoomDevicesResponse(
                            room.getId(),
                            room.getName(),
                            activeDevices,
                            totalPower,
                            deviceResponses
                    );
                })
                .filter(roomResponse -> !roomResponse.devices().isEmpty())
                .toList();
    }
}