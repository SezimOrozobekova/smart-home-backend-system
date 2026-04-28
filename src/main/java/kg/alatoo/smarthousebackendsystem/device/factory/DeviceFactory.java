package kg.alatoo.smarthousebackendsystem.device.factory;

import kg.alatoo.smarthousebackendsystem.device.entity.Device;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceState;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceType;
import kg.alatoo.smarthousebackendsystem.room.entity.Room;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Component
public class DeviceFactory {

    public Device createDevice(
            Room room,
            DeviceType deviceType,
            String name,
            String externalId,
            String model,
            String firmwareVersion,
            Boolean isActive
    ) {
        Device device = new Device();

        device.setRoom(room);
        device.setDeviceType(deviceType);
        device.setName(name);
        device.setExternalId(externalId);
        device.setModel(model);
        device.setFirmwareVersion(firmwareVersion);
        device.setIsActive(isActive != null ? isActive : true);

        return device;
    }

    public Device createLayoutDevice(
            Room room,
            DeviceType deviceType,
            String name
    ) {
        Device device = new Device();

        device.setRoom(room);
        device.setDeviceType(deviceType);
        device.setName(name);
        device.setIsActive(true);

        return device;
    }

    public DeviceState createDefaultState(Device device) {
        DeviceState state = new DeviceState();

        state.setDevice(device);
        state.setIsOnline(false);
        state.setIsOn(false);
        state.setPowerWatts(BigDecimal.ZERO);
        state.setPeakCapacityWatts(BigDecimal.ZERO);
        state.setLastSeenAt(Instant.now());

        return state;
    }
}