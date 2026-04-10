package kg.alatoo.smarthousebackendsystem.device.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceCategory;

public record CreateDeviceTypeRequest(
        @NotBlank String code,
        @NotBlank String name,
        @NotNull DeviceCategory category,
        String icon,
        @NotNull Boolean isControllable,
        @NotNull Boolean isActive
) {
}