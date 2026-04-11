package kg.alatoo.smarthousebackendsystem.device.payload.response;

import com.fasterxml.jackson.databind.JsonNode;

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
        JsonNode rawState,
        Instant recordedAt
) {
}