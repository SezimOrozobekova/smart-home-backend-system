package kg.alatoo.smarthousebackendsystem.device.mapper;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceType;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceTypeResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeviceTypeMapper {
    DeviceTypeResponse toResponse(DeviceType deviceType);
}