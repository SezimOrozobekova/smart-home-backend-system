package kg.alatoo.smarthousebackendsystem.device.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.alatoo.smarthousebackendsystem.device.entity.ShellyStatusSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ShellyHttpClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ShellyStatusSnapshot getStatus(String ipAddress) {
        try {
            String url = "http://" + ipAddress + "/rpc/Shelly.GetStatus";
            String response = restTemplate.getForObject(url, String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode sw = root.path("switch:0");

            return new ShellyStatusSnapshot(
                    sw.path("output").asBoolean(false),
                    getDecimal(sw, "apower"),
                    getDecimal(sw, "voltage"),
                    getDecimal(sw, "current"),
                    getDecimal(sw.path("aenergy"), "total"),
                    getDecimal(sw.path("temperature"), "tC")
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch Shelly status", e);
        }
    }

    private BigDecimal getDecimal(JsonNode node, String fieldName) {
        JsonNode value = node.path(fieldName);
        if (value.isMissingNode() || value.isNull()) {
            return null;
        }
        return value.isNumber()
                ? value.decimalValue()
                : null;
    }
}