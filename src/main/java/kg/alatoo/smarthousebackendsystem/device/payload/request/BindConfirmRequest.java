package kg.alatoo.smarthousebackendsystem.device.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnectionType;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceProvider;

public record BindConfirmRequest(
        @NotNull DeviceProvider provider,
        @NotNull DeviceConnectionType connectionType,
        @NotBlank String externalDeviceId
) {
}