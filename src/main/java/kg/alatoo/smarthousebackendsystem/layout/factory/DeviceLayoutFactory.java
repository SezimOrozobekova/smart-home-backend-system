package kg.alatoo.smarthousebackendsystem.layout.factory;

import kg.alatoo.smarthousebackendsystem.device.entity.Device;
import kg.alatoo.smarthousebackendsystem.layout.entity.DeviceLayout;
import kg.alatoo.smarthousebackendsystem.layout.payload.request.SaveRoomLayoutItemRequest;
import org.springframework.stereotype.Component;

@Component
public class DeviceLayoutFactory {

    public DeviceLayout createOrUpdateLayout(
            DeviceLayout existing,
            Device device,
            SaveRoomLayoutItemRequest item
    ) {
        DeviceLayout layout = existing != null ? existing : new DeviceLayout();

        layout.setDevice(device);

        layout.setPositionX(item.positionX());
        layout.setPositionY(item.positionY());
        layout.setPositionZ(item.positionZ());

        layout.setRotationX(item.rotationX());
        layout.setRotationY(item.rotationY());
        layout.setRotationZ(item.rotationZ());

        layout.setScaleX(item.scaleX());
        layout.setScaleY(item.scaleY());
        layout.setScaleZ(item.scaleZ());

        return layout;
    }
}