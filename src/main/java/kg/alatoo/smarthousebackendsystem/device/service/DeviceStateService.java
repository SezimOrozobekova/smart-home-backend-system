package kg.alatoo.smarthousebackendsystem.device.service;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceState;
import kg.alatoo.smarthousebackendsystem.device.mapper.DeviceStateMapper;
import kg.alatoo.smarthousebackendsystem.device.payload.request.UpdateDeviceStateRequest;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceStateResponse;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceStateService {

    private final DeviceStateRepository deviceStateRepository;
    private final DeviceStateMapper deviceStateMapper;

    public DeviceStateResponse getByDeviceId(UUID deviceId) {
        DeviceState state = deviceStateRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device state not found"));
        return deviceStateMapper.toResponse(state);
    }

    @Transactional
    public DeviceStateResponse updateByDeviceId(UUID deviceId, UpdateDeviceStateRequest request) {
        DeviceState state = deviceStateRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device state not found"));

        if (request.isOnline() != null) state.setIsOnline(request.isOnline());
        if (request.isOn() != null) state.setIsOn(request.isOn());
        if (request.powerWatts() != null) state.setPowerWatts(request.powerWatts());
        if (request.peakCapacityWatts() != null) state.setPeakCapacityWatts(request.peakCapacityWatts());
        if (request.lastSeenAt() != null) state.setLastSeenAt(request.lastSeenAt());
        if (request.rawState() != null) state.setRawState(request.rawState());

        return deviceStateMapper.toResponse(deviceStateRepository.save(state));
    }
}