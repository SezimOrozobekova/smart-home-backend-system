package kg.alatoo.smarthousebackendsystem.device.mapper;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnection;
import kg.alatoo.smarthousebackendsystem.device.payload.response.DeviceConnectionResponse;
import org.springframework.stereotype.Component;

@Component
public class DeviceConnectionMapper {

    public DeviceConnectionResponse toResponse(DeviceConnection connection) {
        return new DeviceConnectionResponse(
                connection.getId(),
                connection.getDevice().getId(),
                connection.getProvider(),
                connection.getConnectionType(),
                connection.getIpAddress(),
                connection.getPort(),
                connection.getUsername(),
                connection.getExternalId(),
                connection.getIsEnabled(),
                connection.getCreatedAt(),
                connection.getUpdatedAt()
        );
    }
}