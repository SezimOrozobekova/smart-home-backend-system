package kg.alatoo.smarthousebackendsystem.device.mapper;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceCommand;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceCommandResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeviceCommandMapper {

    @Mapping(source = "device.id", target = "deviceId")
    @Mapping(source = "issuedBy.id", target = "issuedBy")
    DeviceCommandResponse toResponse(DeviceCommand deviceCommand);
}