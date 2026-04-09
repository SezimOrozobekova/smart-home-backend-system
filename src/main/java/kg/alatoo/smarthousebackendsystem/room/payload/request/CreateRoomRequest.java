package kg.alatoo.smarthousebackendsystem.room.payload.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateRoomRequest(
        @NotBlank String name,
        @NotNull UUID homeId
) {
}