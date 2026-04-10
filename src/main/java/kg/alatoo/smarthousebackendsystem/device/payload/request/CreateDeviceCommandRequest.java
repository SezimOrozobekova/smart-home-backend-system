package kg.alatoo.smarthousebackendsystem.device.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateDeviceCommandRequest(
        @NotNull UUID deviceId,
        @NotNull UUID issuedBy,
        @NotBlank String command,
        String payload
) {
}