package kg.alatoo.smarthousebackendsystem.device.mapper;

import kg.alatoo.smarthousebackendsystem.device.entity.Device;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceResponse;
import kg.alatoo.smarthousebackendsystem.home.payload.response.DeviceItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface DeviceMapper {

    @Mapping(source = "room.name", target = "roomName")
    @Mapping(source = "deviceType.id", target = "deviceTypeId")
    @Mapping(source = "deviceType.code", target = "deviceTypeCode")
    @Mapping(source = "deviceType.name", target = "deviceTypeName")
    DeviceResponse toResponse(Device device);

    @Mapping(source = "device.id", target = "id")
    @Mapping(source = "device.name", target = "name")
    @Mapping(source = "device.deviceType.name", target = "type")
    @Mapping(source = "roomName", target = "room")
    @Mapping(source = "power", target = "power")
    @Mapping(source = "basePower", target = "basePower")
    @Mapping(source = "active", target = "active")
    @Mapping(source = "online", target = "online")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "isOn", target = "isOn")
    DeviceItemResponse toResponse(
            Device device,
            String roomName,
            Integer power,
            Integer basePower,
            Boolean active,
            Boolean online,
            Instant updatedAt,
            Boolean isOn
    );
}