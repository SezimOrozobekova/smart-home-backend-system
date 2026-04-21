package kg.alatoo.smarthousebackendsystem.device.service.control;

import kg.alatoo.smarthousebackendsystem.device.entity.Device;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnection;
import kg.alatoo.smarthousebackendsystem.device.mapper.DeviceConnectionMapper;
import kg.alatoo.smarthousebackendsystem.device.payload.request.UpsertDeviceConnectionRequest;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceConnectionResponse;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceConnectionRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceConnectionService {

    private final DeviceRepository deviceRepository;
    private final DeviceConnectionRepository deviceConnectionRepository;
    private final DeviceConnectionMapper deviceConnectionMapper;

    public DeviceConnectionResponse getByDeviceId(UUID userId, UUID deviceId) {
        DeviceConnection connection = deviceConnectionRepository
                .findByDeviceIdAndDeviceRoomHomeOwnerId(deviceId, userId)
                .orElseThrow(() -> new RuntimeException("Device connection not found"));

        return deviceConnectionMapper.toResponse(connection);
    }

    @Transactional
    public DeviceConnectionResponse upsert(UUID userId, UUID deviceId, UpsertDeviceConnectionRequest request) {
        Device device = deviceRepository.findByIdAndRoomHomeOwnerId(deviceId, userId)
                .orElseThrow(() -> new RuntimeException("Device not found or access denied"));

        DeviceConnection connection = deviceConnectionRepository.findByDeviceId(deviceId)
                .orElseGet(DeviceConnection::new);

        connection.setDevice(device);
        connection.setProvider(request.provider());
        connection.setConnectionType(request.connectionType());
        connection.setExternalDeviceId(request.externalDeviceId());
        connection.setConfigJson(request.configJson() != null ? request.configJson() : "{}");
        connection.setCredentialsJson(request.credentialsJson());
        connection.setIsEnabled(request.isEnabled() != null ? request.isEnabled() : true);

        DeviceConnection saved = deviceConnectionRepository.save(connection);
        return deviceConnectionMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID userId, UUID deviceId) {
        deviceRepository.findByIdAndRoomHomeOwnerId(deviceId, userId)
                .orElseThrow(() -> new RuntimeException("Device not found or access denied"));

        deviceConnectionRepository.deleteByDeviceId(deviceId);
    }
}