package kg.alatoo.smarthousebackendsystem.device.payload.request;

import jakarta.validation.constraints.NotNull;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnectionType;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceProvider;

public record BindInitRequest(
        @NotNull DeviceProvider provider,
        @NotNull DeviceConnectionType connectionType
) {
}