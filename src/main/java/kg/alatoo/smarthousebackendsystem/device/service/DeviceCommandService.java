package kg.alatoo.smarthousebackendsystem.device.service;

import kg.alatoo.smarthousebackendsystem.device.entity.Device;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceCommand;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceCommandStatus;
import kg.alatoo.smarthousebackendsystem.device.mapper.DeviceCommandMapper;
import kg.alatoo.smarthousebackendsystem.device.payload.request.CreateDeviceCommandRequest;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceCommandResponse;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceCommandRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceRepository;
import kg.alatoo.smarthousebackendsystem.user.entity.User;
import kg.alatoo.smarthousebackendsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceCommandService {

    private final DeviceCommandRepository deviceCommandRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final DeviceCommandMapper deviceCommandMapper;

    public List<DeviceCommandResponse> getAllByDeviceId(UUID deviceId) {
        return deviceCommandRepository.findAllByDeviceIdOrderByIssuedAtDesc(deviceId)
                .stream()
                .map(deviceCommandMapper::toResponse)
                .toList();
    }

    @Transactional
    public DeviceCommandResponse create(CreateDeviceCommandRequest request) {
        Device device = deviceRepository.findById(request.deviceId())
                .orElseThrow(() -> new RuntimeException("Device not found"));

        User user = userRepository.findById(request.issuedBy())
                .orElseThrow(() -> new RuntimeException("User not found"));

        DeviceCommand command = new DeviceCommand();
        command.setDevice(device);
        command.setIssuedBy(user);
        command.setCommand(request.command());
        command.setPayload(request.payload());
        command.setStatus(DeviceCommandStatus.PENDING);

        return deviceCommandMapper.toResponse(deviceCommandRepository.save(command));
    }
}