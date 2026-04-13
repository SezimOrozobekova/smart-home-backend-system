package kg.alatoo.smarthousebackendsystem.home.payload.request;

import jakarta.validation.constraints.NotBlank;

public record CreateHomeRequest(
        @NotBlank
        String name,

        String address
) {
}