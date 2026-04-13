package kg.alatoo.smarthousebackendsystem.device.payload.request;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.time.Instant;

public record UpdateDeviceStateRequest(
        Boolean isOnline,
        Boolean isOn,
        BigDecimal powerWatts,
        BigDecimal peakCapacityWatts,
        Instant lastSeenAt,
        JsonNode rawState
) {
}