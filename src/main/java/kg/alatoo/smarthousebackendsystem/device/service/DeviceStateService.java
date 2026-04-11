package kg.alatoo.smarthousebackendsystem.device.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    public DeviceStateResponse getByDeviceId(UUID deviceId) {
        DeviceState state = deviceStateRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device state not found"));

        return deviceStateMapper.toResponse(state);
    }

    @Transactional
    public DeviceStateResponse updateByDeviceId(UUID deviceId, UpdateDeviceStateRequest request) {

        DeviceState state = deviceStateRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device state not found"));

        if (request.isOnline() != null) {
            state.setIsOnline(request.isOnline());
        }

        if (request.isOn() != null) {
            state.setIsOn(request.isOn());
        }

        if (request.powerWatts() != null) {
            state.setPowerWatts(request.powerWatts());
        }

        if (request.peakCapacityWatts() != null) {
            state.setPeakCapacityWatts(request.peakCapacityWatts());
        }

        if (request.lastSeenAt() != null) {
            state.setLastSeenAt(request.lastSeenAt());
        }

        if (request.rawState() != null) {
            try {
                JsonNode node = objectMapper.readTree(request.rawState());
                state.setRawState(node);
            } catch (Exception e) {
                throw new RuntimeException("Invalid JSON in rawState", e);
            }
        }

        DeviceState saved = deviceStateRepository.save(state);

        return deviceStateMapper.toResponse(saved);
    }

    @Transactional
    public DeviceStateResponse toggleDevice(UUID deviceId) {
        DeviceState state = deviceStateRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device state not found"));

        boolean newIsOn = !Boolean.TRUE.equals(state.getIsOn());
        state.setIsOn(newIsOn);

        if (newIsOn) {
            state.setPowerWatts(
                    state.getPeakCapacityWatts() != null
                            ? state.getPeakCapacityWatts()
                            : java.math.BigDecimal.ZERO
            );
            state.setIsOnline(true);
        } else {
            state.setPowerWatts(java.math.BigDecimal.ZERO);
        }

        state.setLastSeenAt(java.time.Instant.now());

        if (state.getRawState() != null) {
            try {
                com.fasterxml.jackson.databind.node.ObjectNode raw =
                        state.getRawState().isObject()
                                ? (com.fasterxml.jackson.databind.node.ObjectNode) state.getRawState()
                                : objectMapper.createObjectNode();

                raw.put("isOn", state.getIsOn());
                raw.put("isOnline", state.getIsOnline());
                raw.put("powerWatts", state.getPowerWatts() != null ? state.getPowerWatts().doubleValue() : 0);
                raw.put("toggledAt", java.time.Instant.now().toString());

                state.setRawState(raw);
            } catch (Exception e) {
                throw new RuntimeException("Failed to update rawState", e);
            }
        }

        DeviceState saved = deviceStateRepository.save(state);
        return deviceStateMapper.toResponse(saved);
    }
}