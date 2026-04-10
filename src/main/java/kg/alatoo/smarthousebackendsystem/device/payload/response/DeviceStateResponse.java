package kg.alatoo.smarthousebackendsystem.device.payload.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DeviceStateResponse(
        UUID id,
        UUID deviceId,
        Boolean isOnline,
        Boolean isOn,
        BigDecimal powerWatts,
        BigDecimal peakCapacityWatts,
        Instant lastSeenAt,
        String rawState,
        Instant recordedAt
) {
}