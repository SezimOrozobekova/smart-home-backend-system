package kg.alatoo.smarthousebackendsystem.device.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateDeviceRequest(
        @NotNull UUID roomId,
        @NotNull UUID deviceTypeId,
        @NotBlank String name,
        String externalId,
        String model,
        String firmwareVersion,
        @NotNull Boolean isActive
) {
}