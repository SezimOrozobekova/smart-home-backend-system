package kg.alatoo.smarthousebackendsystem.device.payload.request;

import jakarta.validation.constraints.NotNull;

public record SetDevicePowerRequest(
        @NotNull Boolean on
) {
}