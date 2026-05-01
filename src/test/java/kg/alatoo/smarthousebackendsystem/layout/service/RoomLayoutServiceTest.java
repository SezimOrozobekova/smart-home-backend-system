package kg.alatoo.smarthousebackendsystem.layout.service;

import kg.alatoo.smarthousebackendsystem.device.entity.Device;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceState;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceType;
import kg.alatoo.smarthousebackendsystem.device.factory.DeviceFactory;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceStateRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceTypeRepository;
import kg.alatoo.smarthousebackendsystem.layout.entity.DeviceLayout;
import kg.alatoo.smarthousebackendsystem.layout.factory.DeviceLayoutFactory;
import kg.alatoo.smarthousebackendsystem.layout.payload.request.SaveRoomLayoutItemRequest;
import kg.alatoo.smarthousebackendsystem.layout.payload.request.SaveRoomLayoutRequest;
import kg.alatoo.smarthousebackendsystem.layout.payload.response.RoomLayoutResponse;
import kg.alatoo.smarthousebackendsystem.layout.repository.DeviceLayoutRepository;
import kg.alatoo.smarthousebackendsystem.room.entity.Room;
import kg.alatoo.smarthousebackendsystem.room.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomLayoutServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceTypeRepository deviceTypeRepository;

    @Mock
    private DeviceLayoutRepository deviceLayoutRepository;

    @Mock
    private DeviceStateRepository deviceStateRepository;

    @Mock
    private DeviceFactory deviceFactory;

    @Mock
    private DeviceLayoutFactory deviceLayoutFactory;

    @InjectMocks
    private RoomLayoutService roomLayoutService;

    @Test
    void getRoomLayout_shouldReturnRoomLayout() {
        UUID userId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();
        UUID deviceId = UUID.randomUUID();
        UUID deviceTypeId = UUID.randomUUID();

        Room room = new Room();
        room.setId(roomId);
        room.setName("Living room");
        room.setRoomWidth(5);
        room.setRoomDepth(4);

        DeviceType deviceType = new DeviceType();
        deviceType.setId(deviceTypeId);
        deviceType.setCode("LIGHT");
        deviceType.setName("Light");

        Device device = new Device();
        device.setId(deviceId);
        device.setName("Main light");
        device.setDeviceType(deviceType);
        device.setIsActive(true);

        DeviceLayout layout = new DeviceLayout();
        layout.setDevice(device);
        layout.setPositionX(1.0);
        layout.setPositionY(2.0);
        layout.setPositionZ(3.0);
        layout.setRotationX(0.0);
        layout.setRotationY(0.0);
        layout.setRotationZ(0.0);
        layout.setScaleX(1.0);
        layout.setScaleY(1.0);
        layout.setScaleZ(1.0);

        when(roomRepository.findByIdAndHomeOwnerId(roomId, userId))
                .thenReturn(Optional.of(room));

        when(deviceLayoutRepository.findAllByDeviceRoomId(roomId))
                .thenReturn(List.of(layout));

        RoomLayoutResponse response = roomLayoutService.getRoomLayout(userId, roomId);

        assertEquals(roomId, response.roomId());
        assertEquals("Living room", response.roomName());
        assertEquals(5, response.roomWidth());
        assertEquals(4, response.roomDepth());
        assertEquals(1, response.items().size());

        assertEquals(deviceId, response.items().get(0).deviceId());
        assertEquals(deviceTypeId, response.items().get(0).deviceTypeId());
        assertEquals("LIGHT", response.items().get(0).deviceTypeCode());
        assertEquals("Light", response.items().get(0).deviceTypeName());
        assertEquals("Main light", response.items().get(0).name());

        assertEquals(1.0, response.items().get(0).positionX());
        assertEquals(2.0, response.items().get(0).positionY());
        assertEquals(3.0, response.items().get(0).positionZ());
        assertEquals(0.0, response.items().get(0).rotationX());
        assertEquals(0.0, response.items().get(0).rotationY());
        assertEquals(0.0, response.items().get(0).rotationZ());
        assertEquals(1.0, response.items().get(0).scaleX());
        assertEquals(1.0, response.items().get(0).scaleY());
        assertEquals(1.0, response.items().get(0).scaleZ());
        assertTrue(response.items().get(0).isActive());
    }

    @Test
    void getRoomLayout_shouldThrowException_whenRoomNotFound() {
        UUID userId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();

        when(roomRepository.findByIdAndHomeOwnerId(roomId, userId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> roomLayoutService.getRoomLayout(userId, roomId)
        );

        assertEquals("Room not found or access denied", exception.getMessage());

        verify(roomRepository).findByIdAndHomeOwnerId(roomId, userId);
        verifyNoInteractions(deviceLayoutRepository);
    }

    @Test
    void saveRoomLayout_shouldCreateNewDeviceStateAndLayout() {
        UUID userId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();
        UUID deviceTypeId = UUID.randomUUID();
        UUID newDeviceId = UUID.randomUUID();

        Room room = new Room();
        room.setId(roomId);
        room.setName("Bedroom");

        DeviceType deviceType = new DeviceType();
        deviceType.setId(deviceTypeId);
        deviceType.setCode("PLUG");
        deviceType.setName("Smart Plug");

        Device newDevice = new Device();
        newDevice.setId(newDeviceId);
        newDevice.setName("Desk plug");
        newDevice.setDeviceType(deviceType);
        newDevice.setRoom(room);
        newDevice.setIsActive(true);

        DeviceState defaultState = new DeviceState();

        SaveRoomLayoutItemRequest item = new SaveRoomLayoutItemRequest(
                null,
                deviceTypeId,
                "Desk plug",
                1.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                1.0,
                1.0,
                1.0
        );

        SaveRoomLayoutRequest request = new SaveRoomLayoutRequest(
                6,
                5,
                List.of(item)
        );

        DeviceLayout createdLayout = new DeviceLayout();
        createdLayout.setDevice(newDevice);

        when(roomRepository.findByIdAndHomeOwnerId(roomId, userId))
                .thenReturn(Optional.of(room));

        when(deviceRepository.findAllByRoomId(roomId))
                .thenReturn(List.of());

        when(deviceTypeRepository.findById(deviceTypeId))
                .thenReturn(Optional.of(deviceType));

        when(deviceFactory.createLayoutDevice(room, deviceType, "Desk plug"))
                .thenReturn(newDevice);

        when(deviceRepository.save(newDevice))
                .thenReturn(newDevice);

        when(deviceFactory.createDefaultState(newDevice))
                .thenReturn(defaultState);

        when(deviceLayoutRepository.findByDeviceId(newDeviceId))
                .thenReturn(Optional.empty());

        when(deviceLayoutFactory.createOrUpdateLayout(null, newDevice, item))
                .thenReturn(createdLayout);

        roomLayoutService.saveRoomLayout(userId, roomId, request);

        assertEquals(6, room.getRoomWidth());
        assertEquals(5, room.getRoomDepth());

        verify(roomRepository).save(room);
        verify(deviceRepository).save(newDevice);
        verify(deviceStateRepository).save(defaultState);
        verify(deviceLayoutRepository).save(createdLayout);
    }

    @Test
    void saveRoomLayout_shouldDeactivateExistingDevice_whenItIsRemovedFromLayout() {
        UUID userId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();
        UUID existingDeviceId = UUID.randomUUID();

        Room room = new Room();
        room.setId(roomId);

        Device existingDevice = new Device();
        existingDevice.setId(existingDeviceId);
        existingDevice.setIsActive(true);

        SaveRoomLayoutRequest request = new SaveRoomLayoutRequest(
                4,
                4,
                List.of()
        );

        when(roomRepository.findByIdAndHomeOwnerId(roomId, userId))
                .thenReturn(Optional.of(room));

        when(deviceRepository.findAllByRoomId(roomId))
                .thenReturn(List.of(existingDevice));

        roomLayoutService.saveRoomLayout(userId, roomId, request);

        assertFalse(existingDevice.getIsActive());

        verify(roomRepository).save(room);
        verify(deviceLayoutRepository).deleteByDeviceId(existingDeviceId);
        verify(deviceRepository).save(existingDevice);
    }
}