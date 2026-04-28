package kg.alatoo.smarthousebackendsystem.device.service;

import kg.alatoo.smarthousebackendsystem.device.entity.Device;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceState;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceType;
import kg.alatoo.smarthousebackendsystem.device.factory.DeviceFactory;
import kg.alatoo.smarthousebackendsystem.device.mapper.DeviceMapper;
import kg.alatoo.smarthousebackendsystem.device.payload.request.CreateDeviceRequest;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceResponse;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceConnectionRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceStateRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceTypeRepository;
import kg.alatoo.smarthousebackendsystem.layout.repository.DeviceLayoutRepository;
import kg.alatoo.smarthousebackendsystem.room.entity.Room;
import kg.alatoo.smarthousebackendsystem.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceTypeRepository deviceTypeRepository;
    private final DeviceStateRepository deviceStateRepository;
    private final DeviceConnectionRepository deviceConnectionRepository;
    private final DeviceLayoutRepository deviceLayoutRepository;
    private final RoomRepository roomRepository;
    private final DeviceMapper deviceMapper;
    private final DeviceFactory deviceFactory;

    public List<DeviceResponse> getAllByRoom(UUID roomId) {
        return deviceRepository.findAllByRoomId(roomId)
                .stream()
                .map(deviceMapper::toResponse)
                .toList();
    }

    public List<DeviceResponse> getMyDevices(UUID userId) {
        return deviceRepository.findAllByRoomHomeOwnerId(userId)
                .stream()
                .map(deviceMapper::toResponse)
                .toList();
    }

    public DeviceResponse getById(UUID id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        return deviceMapper.toResponse(device);
    }

    @Transactional
    public DeviceResponse create(CreateDeviceRequest request) {
        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        DeviceType deviceType = deviceTypeRepository.findById(request.deviceTypeId())
                .orElseThrow(() -> new RuntimeException("Device type not found"));

        Device device = deviceFactory.createDevice(
                room,
                deviceType,
                request.name(),
                request.externalId(),
                request.model(),
                request.firmwareVersion(),
                request.isActive()
        );

        Device savedDevice = deviceRepository.save(device);
        DeviceState state = deviceFactory.createDefaultState(savedDevice);
        deviceStateRepository.save(state);

        return deviceMapper.toResponse(savedDevice);
    }

    @Transactional
    public void deleteDevice(UUID userId, UUID deviceId) {
        Device device = deviceRepository.findByIdAndRoomHomeOwnerId(deviceId, userId)
                .orElseThrow(() -> new RuntimeException("Device not found or access denied"));

        deviceConnectionRepository.deleteByDeviceId(deviceId);
        deviceStateRepository.deleteByDeviceId(deviceId);
        deviceLayoutRepository.deleteByDeviceId(deviceId);
        deviceRepository.delete(device);
    }
}