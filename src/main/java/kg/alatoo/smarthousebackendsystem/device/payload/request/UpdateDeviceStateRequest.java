package kg.alatoo.smarthousebackendsystem.device.payload.request;

import java.math.BigDecimal;
import java.time.Instant;

public record UpdateDeviceStateRequest(
        Boolean isOnline,
        Boolean isOn,
        BigDecimal powerWatts,
        BigDecimal peakCapacityWatts,
        Instant lastSeenAt,
        String rawState
) {
}