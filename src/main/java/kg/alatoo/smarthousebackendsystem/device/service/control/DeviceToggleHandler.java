package kg.alatoo.smarthousebackendsystem.device.service.control;

import kg.alatoo.smarthousebackendsystem.device.entity.DeviceConnection;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceState;

public interface DeviceToggleHandler {
    boolean supports(DeviceConnection connection);
    DeviceState toggle(DeviceState state, DeviceConnection connection, boolean nextOn);
}