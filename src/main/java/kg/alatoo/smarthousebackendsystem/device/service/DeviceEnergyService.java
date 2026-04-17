//package kg.alatoo.smarthousebackendsystem.device.service;
//
//import kg.alatoo.smarthousebackendsystem.device.entity.*;
//import kg.alatoo.smarthousebackendsystem.device.payload.response.EnergyPointResponse;
//import kg.alatoo.smarthousebackendsystem.device.payload.response.MonthlyEnergyResponse;
//import kg.alatoo.smarthousebackendsystem.device.repository.DeviceConnectionRepository;
//import kg.alatoo.smarthousebackendsystem.device.repository.DeviceEnergyHistoryRepository;
//import kg.alatoo.smarthousebackendsystem.device.repository.DeviceRepository;
//import kg.alatoo.smarthousebackendsystem.device.repository.DeviceStateRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.*;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class DeviceEnergyService {
//
//    private final DeviceRepository deviceRepository;
//    private final DeviceConnectionRepository deviceConnectionRepository;
//    private final DeviceStateRepository deviceStateRepository;
//    private final DeviceEnergyHistoryRepository deviceEnergyHistoryRepository;
//    private final ShellyHttpClient shellyHttpClient;
//
//    @Transactional
//    @Scheduled(cron = "0 * * * * *") // каждую минуту
//    public void collectEnergySnapshots() {
//        List<DeviceConnection> connections = deviceConnectionRepository.findAll()
//                .stream()
//                .filter(c -> Boolean.TRUE.equals(c.getIsEnabled()))
//                .filter(c -> c.getConnectionType() == DeviceConnectionType.LOCAL_HTTP)
//                .toList();
//
//        for (DeviceConnection connection : connections) {
//            try {
//                collectForConnection(connection);
//            } catch (Exception e) {
//                // можно потом заменить на logger.warn(...)
//                System.err.println("Failed to collect energy for device: " + connection.getDevice().getId());
//            }
//        }
//    }
//
//    @Transactional
//    protected void collectForConnection(DeviceConnection connection) {
//        if (connection.getIpAddress() == null || connection.getIpAddress().isBlank()) {
//            return;
//        }
//
//        ShellyStatusSnapshot snapshot = shellyHttpClient.getStatus(connection.getIpAddress());
//        Device device = connection.getDevice();
//
//        saveHistory(device, snapshot);
//        updateDeviceState(device, snapshot);
//    }
//
//    private void saveHistory(Device device, ShellyStatusSnapshot snapshot) {
//        DeviceEnergyHistory history = new DeviceEnergyHistory();
//        history.setDevice(device);
//        history.setRecordedAt(Instant.now());
//        history.setPowerWatts(snapshot.powerWatts());
//        history.setVoltage(snapshot.voltage());
//        history.setCurrent(snapshot.current());
//        history.setTotalEnergyWh(snapshot.totalEnergyWh());
//        history.setTemperatureC(snapshot.temperatureC());
//
//        deviceEnergyHistoryRepository.save(history);
//    }
//
//    private void updateDeviceState(Device device, ShellyStatusSnapshot snapshot) {
//        DeviceState state = deviceStateRepository.findByDeviceId(device.getId())
//                .orElseGet(() -> {
//                    DeviceState newState = new DeviceState();
//                    newState.setDevice(device);
//                    return newState;
//                });
//
//        state.setIsOn(snapshot.isOn());
//        state.setIsOnline(true);
//        state.setPowerWatts(snapshot.powerWatts());
//        state.setLastSeenAt(Instant.now());
//
//        deviceStateRepository.save(state);
//    }
//}