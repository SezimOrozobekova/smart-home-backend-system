package kg.alatoo.smarthousebackendsystem.device.mapper;

import kg.alatoo.smarthousebackendsystem.device.entity.Device;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeviceMapper {

    @Mapping(source = "room.name", target = "roomName")
    @Mapping(source = "deviceType.id", target = "deviceTypeId")
    @Mapping(source = "deviceType.code", target = "deviceTypeCode")
    @Mapping(source = "deviceType.name", target = "deviceTypeName")
    DeviceResponse toResponse(Device device);
}