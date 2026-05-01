package kg.alatoo.smarthousebackendsystem.device.service;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnection;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceState;
import kg.alatoo.smarthousebackendsystem.device.mapper.DeviceStateMapper;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceStateResponse;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceConnectionRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceStateRepository;
import kg.alatoo.smarthousebackendsystem.device.service.control.DeviceControlService;
import kg.alatoo.smarthousebackendsystem.device.service.control.DeviceToggleCoordinator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceControlServiceTest {

    @Mock
    private DeviceStateRepository deviceStateRepository;

    @Mock
    private DeviceConnectionRepository deviceConnectionRepository;

    @Mock
    private DeviceStateMapper deviceStateMapper;

    @Mock
    private DeviceToggleCoordinator deviceToggleCoordinator;

    @InjectMocks
    private DeviceControlService deviceControlService;

    @Test
    void setDevicePower_shouldToggleDeviceAndReturnResponse() {
        UUID deviceId = UUID.randomUUID();

        DeviceState state = new DeviceState();
        DeviceConnection connection = new DeviceConnection();
        DeviceState updatedState = new DeviceState();

        DeviceStateResponse response = mock(DeviceStateResponse.class);

        when(deviceStateRepository.findByDeviceId(deviceId))
                .thenReturn(Optional.of(state));

        when(deviceConnectionRepository.findByDeviceId(deviceId))
                .thenReturn(Optional.of(connection));

        when(deviceToggleCoordinator.toggle(state, connection, true))
                .thenReturn(updatedState);

        when(deviceStateMapper.toResponse(updatedState))
                .thenReturn(response);

        DeviceStateResponse result = deviceControlService.setDevicePower(deviceId, true);

        assertSame(response, result);

        verify(deviceStateRepository).findByDeviceId(deviceId);
        verify(deviceConnectionRepository).findByDeviceId(deviceId);
        verify(deviceToggleCoordinator).toggle(state, connection, true);
        verify(deviceStateMapper).toResponse(updatedState);
    }

    @Test
    void setDevicePower_shouldThrowException_whenDeviceStateNotFound() {
        UUID deviceId = UUID.randomUUID();

        when(deviceStateRepository.findByDeviceId(deviceId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> deviceControlService.setDevicePower(deviceId, true)
        );

        assertEquals("Device state not found", exception.getMessage());

        verify(deviceStateRepository).findByDeviceId(deviceId);
        verifyNoInteractions(deviceConnectionRepository);
        verifyNoInteractions(deviceToggleCoordinator);
        verifyNoInteractions(deviceStateMapper);
    }

    @Test
    void setDevicePower_shouldThrowException_whenDeviceConnectionNotFound() {
        UUID deviceId = UUID.randomUUID();

        DeviceState state = new DeviceState();

        when(deviceStateRepository.findByDeviceId(deviceId))
                .thenReturn(Optional.of(state));

        when(deviceConnectionRepository.findByDeviceId(deviceId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> deviceControlService.setDevicePower(deviceId, true)
        );

        assertEquals("Device connection not found", exception.getMessage());

        verify(deviceStateRepository).findByDeviceId(deviceId);
        verify(deviceConnectionRepository).findByDeviceId(deviceId);
        verifyNoInteractions(deviceToggleCoordinator);
        verifyNoInteractions(deviceStateMapper);
    }
}