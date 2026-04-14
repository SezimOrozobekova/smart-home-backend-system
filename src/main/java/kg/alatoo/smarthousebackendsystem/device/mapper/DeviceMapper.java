package kg.alatoo.smarthousebackendsystem.device.mapper;

import kg.alatoo.smarthousebackendsystem.device.entity.Device;
import kg.alatoo.smarthousebackendsystem.home.payload.response.DeviceItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface DeviceMapper {

    @Mapping(source = "device.id", target = "id")
    @Mapping(source = "device.name", target = "name")
    @Mapping(source = "device.deviceType.name", target = "type")
    @Mapping(source = "roomName", target = "roomName")
    @Mapping(source = "power", target = "power")
    @Mapping(source = "basePower", target = "basePower")
    @Mapping(source = "active", target = "active")
    @Mapping(source = "online", target = "online")
    @Mapping(source = "updatedAt", target = "updatedAt")
    DeviceItemResponse toResponse(
            Device device,
            String roomName,
            Integer power,
            Integer basePower,
            Boolean active,
            Boolean online,
            Instant updatedAt
    );
}