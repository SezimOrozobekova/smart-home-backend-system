package kg.alatoo.smarthousebackendsystem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kg.alatoo.smarthousebackendsystem.device.entity.Device;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceCategory;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceState;
import kg.alatoo.smarthousebackendsystem.device.entity.DeviceType;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceStateRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceTypeRepository;
import kg.alatoo.smarthousebackendsystem.home.entity.Home;
import kg.alatoo.smarthousebackendsystem.home.repository.HomeRepository;
import kg.alatoo.smarthousebackendsystem.room.entity.Room;
import kg.alatoo.smarthousebackendsystem.room.repository.RoomRepository;
import kg.alatoo.smarthousebackendsystem.user.entity.Role;
import kg.alatoo.smarthousebackendsystem.user.entity.User;
import kg.alatoo.smarthousebackendsystem.user.entity.UserStatus;
import kg.alatoo.smarthousebackendsystem.user.repository.RoleRepository;
import kg.alatoo.smarthousebackendsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.Instant;

@Configuration
@RequiredArgsConstructor
public class InitialDataConfig {

    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Bean
    CommandLineRunner initData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            HomeRepository homeRepository,
            RoomRepository roomRepository,
            DeviceTypeRepository deviceTypeRepository,
            DeviceRepository deviceRepository,
            DeviceStateRepository deviceStateRepository
    ) {
        return args -> {
            Role rootRole = saveRole(roleRepository, "ROOT_ADMIN");
            Role adminRole = saveRole(roleRepository, "ADMIN");
            Role userRole = saveRole(roleRepository, "USER");

            User root = saveUser(
                    userRepository,
                    "root@smarthouse.local",
                    "Root Admin",
                    "123456",
                    UserStatus.CREATED,
                    rootRole
            );

            User admin = saveUser(
                    userRepository,
                    "admin@smarthouse.local",
                    "Home Admin",
                    "123456",
                    UserStatus.CREATED,
                    adminRole
            );

            User user = saveUser(
                    userRepository,
                    "user@smarthouse.local",
                    "Regular User",
                    "123456",
                    UserStatus.CREATED,
                    userRole
            );

            Home home = saveHome(
                    homeRepository,
                    "My Smart Home",
                    "Bishkek",
                    admin
            );

            Room livingRoom = saveRoom(roomRepository, "Living Room", home);
            Room kitchen = saveRoom(roomRepository, "Kitchen", home);
            Room bathroom = saveRoom(roomRepository, "Bathroom", home);
            Room bedroom = saveRoom(roomRepository, "Bedroom", home);

            DeviceType tvType = saveDeviceType(
                    deviceTypeRepository,
                    "TV",
                    "TV",
                    DeviceCategory.MEDIA,
                    "tv",
                    true
            );

            DeviceType washingMachineType = saveDeviceType(
                    deviceTypeRepository,
                    "WASHING_MACHINE",
                    "Washing Machine",
                    DeviceCategory.APPLIANCE,
                    "washer",
                    true
            );

            DeviceType kettleType = saveDeviceType(
                    deviceTypeRepository,
                    "ELECTRIC_KETTLE",
                    "Electric Kettle",
                    DeviceCategory.APPLIANCE,
                    "kettle",
                    true
            );

            DeviceType fridgeType = saveDeviceType(
                    deviceTypeRepository,
                    "FRIDGE",
                    "Fridge",
                    DeviceCategory.APPLIANCE,
                    "fridge",
                    true
            );

            DeviceType lampType = saveDeviceType(
                    deviceTypeRepository,
                    "LAMP",
                    "Lamp",
                    DeviceCategory.LIGHTING,
                    "lightbulb",
                    true
            );

            DeviceType computerType = saveDeviceType(
                    deviceTypeRepository,
                    "COMPUTER",
                    "Computer",
                    DeviceCategory.MEDIA,
                    "computer",
                    true
            );

            DeviceType electricFanType = saveDeviceType(
                    deviceTypeRepository,
                    "ELECTRIC_FAN",
                    "Electric Fan",
                    DeviceCategory.CLIMATE,
                    "fan",
                    true
            );

            DeviceType microwaveType = saveDeviceType(
                    deviceTypeRepository,
                    "MICROWAVE",
                    "Microwave",
                    DeviceCategory.APPLIANCE,
                    "microwave",
                    true
            );

            DeviceType riceCookerType = saveDeviceType(
                    deviceTypeRepository,
                    "RICE_COOKER",
                    "Rice Cooker",
                    DeviceCategory.APPLIANCE,
                    "rice-bowl",
                    true
            );

            DeviceType toasterType = saveDeviceType(
                    deviceTypeRepository,
                    "TOASTER",
                    "Toaster",
                    DeviceCategory.APPLIANCE,
                    "toast",
                    true
            );

            DeviceType securityCameraType = saveDeviceType(
                    deviceTypeRepository,
                    "SECURITY_CAMERA",
                    "Security Camera",
                    DeviceCategory.SECURITY,
                    "camera",
                    false
            );

            Device tv = saveDevice(
                    deviceRepository,
                    livingRoom,
                    tvType,
                    "TV",
                    "tv-001",
                    "Samsung QLED",
                    "1.0.0",
                    true
            );

            Device computer = saveDevice(
                    deviceRepository,
                    bedroom,
                    computerType,
                    "Computer",
                    "pc-001",
                    "Lenovo ThinkCentre",
                    "1.0.0",
                    true
            );

            Device lamp = saveDevice(
                    deviceRepository,
                    bedroom,
                    lampType,
                    "Lamp",
                    "lamp-001",
                    "Xiaomi Lamp",
                    "1.0.0",
                    true
            );

            Device washingMachine = saveDevice(
                    deviceRepository,
                    bathroom,
                    washingMachineType,
                    "Washing Machine",
                    "wm-001",
                    "LG Washer",
                    "1.0.0",
                    true
            );

            Device kettle = saveDevice(
                    deviceRepository,
                    kitchen,
                    kettleType,
                    "Electric Kettle",
                    "kettle-001",
                    "Philips Kettle",
                    "1.0.0",
                    true
            );

            Device fridge = saveDevice(
                    deviceRepository,
                    kitchen,
                    fridgeType,
                    "Fridge",
                    "fridge-001",
                    "Samsung Fridge",
                    "1.0.0",
                    true
            );



            saveState(
                    deviceStateRepository,
                    tv,
                    true,
                    true,
                    new BigDecimal("120.00"),
                    new BigDecimal("200.00")
            );

            saveState(
                    deviceStateRepository,
                    computer,
                    true,
                    true,
                    new BigDecimal("250.00"),
                    new BigDecimal("400.00")
            );

            saveState(
                    deviceStateRepository,
                    lamp,
                    true,
                    false,
                    new BigDecimal("0.00"),
                    new BigDecimal("20.00")
            );

            saveState(
                    deviceStateRepository,
                    washingMachine,
                    false,
                    false,
                    new BigDecimal("0.00"),
                    new BigDecimal("800.00")
            );

            saveState(
                    deviceStateRepository,
                    kettle,
                    true,
                    true,
                    new BigDecimal("1800.00"),
                    new BigDecimal("2000.00")
            );

            saveState(
                    deviceStateRepository,
                    fridge,
                    true,
                    true,
                    new BigDecimal("150.00"),
                    new BigDecimal("300.00")
            );
        };
    }

    private Role saveRole(RoleRepository repository, String name) {
        return repository.findByName(name)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(name);
                    return repository.save(role);
                });
    }

    private User saveUser(
            UserRepository repository,
            String email,
            String name,
            String rawPassword,
            UserStatus status,
            Role role
    ) {
        return repository.findByEmail(email)
                .orElseGet(() -> {
                    User user = new User();
                    user.setEmail(email);
                    user.setName(name);
                    user.setIsActive(true);
                    user.setPasswordHash(passwordEncoder.encode(rawPassword));
                    user.setStatus(status);
                    user.setRole(role);
                    return repository.save(user);
                });
    }

    private Home saveHome(
            HomeRepository repository,
            String name,
            String address,
            User owner
    ) {
        return repository.findByNameAndOwnerId(name, owner.getId())
                .orElseGet(() -> {
                    Home home = new Home();
                    home.setName(name);
                    home.setAddress(address);
                    home.setOwner(owner);
                    return repository.save(home);
                });
    }

    private Room saveRoom(
            RoomRepository repository,
            String name,
            Home home
    ) {
        return repository.findByNameAndHomeId(name, home.getId())
                .orElseGet(() -> {
                    Room room = new Room();
                    room.setName(name);
                    room.setHome(home);
                    return repository.save(room);
                });
    }

    private DeviceType saveDeviceType(
            DeviceTypeRepository repository,
            String code,
            String name,
            DeviceCategory category,
            String icon,
            boolean isControllable
    ) {
        return repository.findByCode(code)
                .orElseGet(() -> {
                    DeviceType deviceType = new DeviceType();
                    deviceType.setCode(code);
                    deviceType.setName(name);
                    deviceType.setCategory(category);
                    deviceType.setIcon(icon);
                    deviceType.setIsControllable(isControllable);
                    deviceType.setIsActive(true);
                    return repository.save(deviceType);
                });
    }

    private Device saveDevice(
            DeviceRepository repository,
            Room room,
            DeviceType deviceType,
            String name,
            String externalId,
            String model,
            String firmwareVersion,
            boolean isActive
    ) {
        return repository.findByExternalId(externalId)
                .orElseGet(() -> {
                    Device device = new Device();
                    device.setRoom(room);
                    device.setDeviceType(deviceType);
                    device.setName(name);
                    device.setExternalId(externalId);
                    device.setModel(model);
                    device.setFirmwareVersion(firmwareVersion);
                    device.setIsActive(isActive);
                    return repository.save(device);
                });
    }

    private void saveState(
            DeviceStateRepository repository,
            Device device,
            boolean isOnline,
            boolean isOn,
            BigDecimal powerWatts,
            BigDecimal peakCapacityWatts
    ) {
        if (repository.findByDeviceId(device.getId()).isPresent()) {
            return;
        }

        DeviceState state = new DeviceState();
        state.setDevice(device);
        state.setIsOnline(isOnline);
        state.setIsOn(isOn);
        state.setPowerWatts(powerWatts);
        state.setPeakCapacityWatts(peakCapacityWatts);
        state.setLastSeenAt(Instant.now());

        ObjectNode rawState = objectMapper.createObjectNode();
        rawState.put("mock", true);
        rawState.put("deviceName", device.getName());
        rawState.put("isOnline", isOnline);
        rawState.put("isOn", isOn);

        if (powerWatts != null) {
            rawState.put("powerWatts", powerWatts.doubleValue());
        }

        if (peakCapacityWatts != null) {
            rawState.put("peakCapacityWatts", peakCapacityWatts.doubleValue());
        }

        state.setRawState(rawState);
        repository.save(state);
    }
}