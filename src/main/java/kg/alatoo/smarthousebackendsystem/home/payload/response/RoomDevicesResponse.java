package kg.alatoo.smarthousebackendsystem.home.payload.response;

import java.util.List;
import java.util.UUID;

public record RoomDevicesResponse(
        UUID roomId,
        String roomName,
        Integer activeDevices,
        Integer totalPower,
        List<DeviceItemResponse> devices
) {
}