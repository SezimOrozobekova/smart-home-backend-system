package kg.alatoo.smarthousebackendsystem.device.payload.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SaveRoomLayoutRequest(
        @NotNull Integer roomWidth,
        @NotNull Integer roomDepth,
        @NotNull List<SaveRoomLayoutItemRequest> items
) {
}