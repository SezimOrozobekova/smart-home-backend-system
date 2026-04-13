package kg.alatoo.smarthousebackendsystem.layout.payload.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SaveRoomLayoutRequest(
        @NotNull Integer roomWidth,
        @NotNull Integer roomDepth,
        @NotNull @Valid List<SaveRoomLayoutItemRequest> items
) {
}