package kg.alatoo.smarthousebackendsystem.layout.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kg.alatoo.smarthousebackendsystem.device.entity.Device;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceState;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceType;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceStateRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceTypeRepository;
import kg.alatoo.smarthousebackendsystem.layout.entity.DeviceLayout;
import kg.alatoo.smarthousebackendsystem.layout.payload.request.SaveRoomLayoutItemRequest;
import kg.alatoo.smarthousebackendsystem.layout.payload.request.SaveRoomLayoutRequest;
import kg.alatoo.smarthousebackendsystem.layout.payload.response.RoomLayoutItemResponse;
import kg.alatoo.smarthousebackendsystem.layout.payload.response.RoomLayoutResponse;
import kg.alatoo.smarthousebackendsystem.layout.repository.DeviceLayoutRepository;
import kg.alatoo.smarthousebackendsystem.room.entity.Room;
import kg.alatoo.smarthousebackendsystem.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomLayoutService {

    private final RoomRepository roomRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceTypeRepository deviceTypeRepository;
    private final DeviceLayoutRepository deviceLayoutRepository;
    private final DeviceStateRepository deviceStateRepository;
    private final ObjectMapper objectMapper;

    public void saveRoomLayout(UUID userId, UUID roomId, SaveRoomLayoutRequest request) {
        Room room = roomRepository.findByIdAndHomeOwnerId(roomId, userId)
                .orElseThrow(() -> new RuntimeException("Room not found or access denied"));

        room.setRoomWidth(request.roomWidth());
        room.setRoomDepth(request.roomDepth());
        roomRepository.save(room);

        List<Device> existingDevices = deviceRepository.findAllByRoomId(roomId);

        var incomingDeviceIds = request.items().stream()
                .map(SaveRoomLayoutItemRequest::deviceId)
                .filter(id -> id != null)
                .collect(java.util.stream.Collectors.toSet());

        for (Device device : existingDevices) {
            if (!incomingDeviceIds.contains(device.getId())) {
                device.setIsActive(false);
                deviceLayoutRepository.deleteByDeviceId(device.getId());
                deviceRepository.save(device);
            }
        }

        for (SaveRoomLayoutItemRequest item : request.items()) {

            DeviceType deviceType = deviceTypeRepository.findById(item.deviceTypeId())
                    .orElseThrow(() -> new RuntimeException("Device type not found: " + item.deviceTypeId()));

            Device device;

            if (item.deviceId() != null) {

                device = deviceRepository.findByIdAndRoomId(item.deviceId(), roomId)
                        .orElseThrow(() -> new RuntimeException("Device not found in this room"));

                device.setDeviceType(deviceType);
                device.setName(item.name());
                device.setIsActive(true);

                device = deviceRepository.save(device);
            }

            else {
                device = new Device();
                device.setRoom(room);
                device.setDeviceType(deviceType);
                device.setName(item.name());
                device.setIsActive(true);

                device = deviceRepository.save(device);

                DeviceState state = buildInitialDeviceState(device, deviceType);
                deviceStateRepository.save(state);
            }

            DeviceLayout layout = deviceLayoutRepository.findByDeviceId(device.getId())
                    .orElse(null);

            if (layout == null) {
                layout = new DeviceLayout();
                layout.setDevice(device);
            }

            layout.setPositionX(item.positionX());
            layout.setPositionY(item.positionY());
            layout.setPositionZ(item.positionZ());

            layout.setRotationX(item.rotationX());
            layout.setRotationY(item.rotationY());
            layout.setRotationZ(item.rotationZ());

            layout.setScaleX(item.scaleX());
            layout.setScaleY(item.scaleY());
            layout.setScaleZ(item.scaleZ());

            deviceLayoutRepository.save(layout);
        }
    }

    @Transactional(readOnly = true)
    public RoomLayoutResponse getRoomLayout(UUID userId, UUID roomId) {
        Room room = roomRepository.findByIdAndHomeOwnerId(roomId, userId)
                .orElseThrow(() -> new RuntimeException("Room not found or access denied"));

        List<DeviceLayout> layouts = deviceLayoutRepository.findAllByDeviceRoomId(roomId);

        List<RoomLayoutItemResponse> items = layouts.stream()
                .map(this::toLayoutItemResponse)
                .toList();

        return new RoomLayoutResponse(
                room.getId(),
                room.getName(),
                room.getRoomWidth(),
                room.getRoomDepth(),
                items
        );
    }

    @Transactional(readOnly = true)
    public List<RoomLayoutResponse> getMyRoomLayouts(UUID userId) {
        List<Room> rooms = roomRepository.findAllByHomeOwnerId(userId);

        if (rooms.isEmpty()) {
            return List.of();
        }

        return rooms.stream()
                .map(room -> {
                    List<DeviceLayout> layouts = deviceLayoutRepository.findAllByDeviceRoomId(room.getId());

                    List<RoomLayoutItemResponse> items = layouts.stream()
                            .map(this::toLayoutItemResponse)
                            .toList();

                    return new RoomLayoutResponse(
                            room.getId(),
                            room.getName(),
                            room.getRoomWidth(),
                            room.getRoomDepth(),
                            items
                    );
                })
                .toList();
    }

    private RoomLayoutItemResponse toLayoutItemResponse(DeviceLayout layout) {
        Device device = layout.getDevice();
        DeviceType deviceType = device.getDeviceType();

        return new RoomLayoutItemResponse(
                device.getId(),
                deviceType.getId(),
                deviceType.getCode(),
                deviceType.getName(),
                device.getName(),
                layout.getPositionX(),
                layout.getPositionY(),
                layout.getPositionZ(),
                layout.getRotationX(),
                layout.getRotationY(),
                layout.getRotationZ(),
                layout.getScaleX(),
                layout.getScaleY(),
                layout.getScaleZ(),
                device.getIsActive()
        );
    }

    private DeviceState buildInitialDeviceState(Device device, DeviceType deviceType) {
        DeviceState state = new DeviceState();
        state.setDevice(device);
        state.setIsOnline(false);
        state.setIsOn(false);
        state.setPowerWatts(BigDecimal.ZERO);
        state.setPeakCapacityWatts(resolvePeakCapacity(deviceType));
        state.setLastSeenAt(Instant.now());
        state.setRawState(createDefaultRawState());
        return state;
    }

    private BigDecimal resolvePeakCapacity(DeviceType deviceType) {
        if (deviceType == null || deviceType.getCode() == null) {
            return BigDecimal.ZERO;
        }

        return switch (deviceType.getCode().toUpperCase()) {
            case "LAMP" -> BigDecimal.valueOf(10);
            case "FRIDGE" -> BigDecimal.valueOf(150);
            case "MICROWAVE" -> BigDecimal.valueOf(1200);
            case "TV" -> BigDecimal.valueOf(120);
            case "AIR_CONDITIONER" -> BigDecimal.valueOf(1500);
            case "WASHING_MACHINE" -> BigDecimal.valueOf(800);
            default -> BigDecimal.ZERO;
        };
    }

    private JsonNode createDefaultRawState() {
        ObjectNode raw = objectMapper.createObjectNode();
        raw.put("isOnline", false);
        raw.put("isOn", false);
        raw.put("powerWatts", 0);
        raw.put("createdAt", Instant.now().toString());
        return raw;
    }
}