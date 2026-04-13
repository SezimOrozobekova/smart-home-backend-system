package kg.alatoo.smarthousebackendsystem.user.payload.response;

import kg.alatoo.smarthousebackendsystem.user.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String name,
        Boolean isActive,
        UserStatus status,
        Long telegramChatId,
        String roleName,
        Instant createdAt,
        Instant updatedAt
) {
}