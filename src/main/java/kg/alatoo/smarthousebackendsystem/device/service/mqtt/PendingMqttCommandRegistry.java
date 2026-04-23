package kg.alatoo.smarthousebackendsystem.device.service.mqtt;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PendingMqttCommandRegistry {

    private final Map<Integer, PendingMqttCommand> commands = new ConcurrentHashMap<>();

    public void put(PendingMqttCommand command) {
        commands.put(command.requestId(), command);
    }

    public Optional<PendingMqttCommand> get(int requestId) {
        return Optional.ofNullable(commands.get(requestId));
    }

    public void remove(int requestId) {
        commands.remove(requestId);
    }
}