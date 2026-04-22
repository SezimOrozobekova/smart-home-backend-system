package kg.alatoo.smarthousebackendsystem.device.service.control;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnection;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceState;
import kg.alatoo.smarthousebackendsystem.device.mapper.DeviceStateMapper;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceStateResponse;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceConnectionRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceControlService {

    private final DeviceStateRepository deviceStateRepository;
    private final DeviceConnectionRepository deviceConnectionRepository;
    private final DeviceStateMapper deviceStateMapper;
    private final DeviceToggleCoordinator deviceToggleCoordinator;

    @Transactional
    public DeviceStateResponse setDevicePower(UUID deviceId, boolean desiredOn) {
        DeviceState state = deviceStateRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device state not found"));

        DeviceConnection connection = deviceConnectionRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device connection not found"));

        DeviceState updatedState = deviceToggleCoordinator.toggle(state, connection, desiredOn);
        return deviceStateMapper.toResponse(updatedState);
    }
}