package kg.alatoo.smarthousebackendsystem.home.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateHomeRequest(
        @NotBlank String name,
        String address,
        @NotNull UUID ownerId
) {
}