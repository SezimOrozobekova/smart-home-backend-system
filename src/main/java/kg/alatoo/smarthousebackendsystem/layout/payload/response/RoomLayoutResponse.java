package kg.alatoo.smarthousebackendsystem.layout.payload.response;

import java.util.List;
import java.util.UUID;

public record RoomLayoutResponse(
        UUID roomId,
        String roomName,
        Integer roomWidth,
        Integer roomDepth,
        List<RoomLayoutItemResponse> items
) {
}