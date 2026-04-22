package kg.alatoo.smarthousebackendsystem.device.service.control;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnection;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceToggleCoordinator {

    private final List<DeviceToggleHandler> handlers;

    public DeviceState toggle(DeviceState state, DeviceConnection connection, boolean desiredOn) {
        DeviceToggleHandler handler = handlers.stream()
                .filter(h -> h.supports(connection))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "No toggle handler found for provider=%s, connectionType=%s"
                                .formatted(connection.getProvider(), connection.getConnectionType())
                ));

        return handler.toggle(state, connection, desiredOn);
    }
}