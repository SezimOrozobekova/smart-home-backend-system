package kg.alatoo.smarthousebackendsystem.layout.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SaveRoomLayoutItemRequest(
        @NotNull UUID deviceTypeId,
        @NotBlank String name,
        @NotNull Double positionX,
        @NotNull Double positionY,
        @NotNull Double positionZ,
        @NotNull Double rotationX,
        @NotNull Double rotationY,
        @NotNull Double rotationZ,
        @NotNull Double scaleX,
        @NotNull Double scaleY,
        @NotNull Double scaleZ
) {
}