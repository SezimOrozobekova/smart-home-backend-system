package kg.alatoo.smarthousebackendsystem.device.service;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceType;
import kg.alatoo.smarthousebackendsystem.device.mapper.DeviceTypeMapper;
import kg.alatoo.smarthousebackendsystem.device.payload.request.CreateDeviceTypeRequest;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceTypeResponse;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceTypeService {

    private final DeviceTypeRepository deviceTypeRepository;
    private final DeviceTypeMapper deviceTypeMapper;

    public List<DeviceTypeResponse> getAll() {
        return deviceTypeRepository.findAll()
                .stream()
                .map(deviceTypeMapper::toResponse)
                .toList();
    }

    @Transactional
    public DeviceTypeResponse create(CreateDeviceTypeRequest request) {
        if (deviceTypeRepository.existsByCode(request.code())) {
            throw new RuntimeException("Device type with this code already exists");
        }

        DeviceType deviceType = new DeviceType();
        deviceType.setCode(request.code());
        deviceType.setName(request.name());
        deviceType.setCategory(request.category());
        deviceType.setIcon(request.icon());
        deviceType.setIsControllable(request.isControllable());
        deviceType.setIsActive(request.isActive());

        return deviceTypeMapper.toResponse(deviceTypeRepository.save(deviceType));
    }
}