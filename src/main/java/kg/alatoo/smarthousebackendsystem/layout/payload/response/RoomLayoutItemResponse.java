package kg.alatoo.smarthousebackendsystem.layout.payload.response;

import java.util.UUID;

public record RoomLayoutItemResponse(
        UUID deviceId,
        UUID deviceTypeId,
        String deviceTypeCode,
        String deviceTypeName,
        String name,
        Double positionX,
        Double positionY,
        Double positionZ,
        Double rotationX,
        Double rotationY,
        Double rotationZ,
        Double scaleX,
        Double scaleY,
        Double scaleZ,
        Boolean isActive
) {
}