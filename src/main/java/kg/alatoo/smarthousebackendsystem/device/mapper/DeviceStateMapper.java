package kg.alatoo.smarthousebackendsystem.device.mapper;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceState;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceStateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeviceStateMapper {

    @Mapping(source = "device.id", target = "deviceId")
    DeviceStateResponse toResponse(DeviceState deviceState);
}