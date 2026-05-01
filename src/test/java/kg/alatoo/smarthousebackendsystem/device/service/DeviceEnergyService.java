package kg.alatoo.smarthousebackendsystem.device.service;

import kg.alatoo.smarthousebackendsystem.device.payload.request.EnergyPeriod;
import kg.alatoo.smarthousebackendsystem.device.payload.response.EnergyChartPointResponse;
import kg.alatoo.smarthousebackendsystem.device.payload.response.MonthlyEnergyResponse;
import kg.alatoo.smarthousebackendsystem.device.repository.DeviceEnergyHistoryRepository;
import kg.alatoo.smarthousebackendsystem.device.repository.projection.EnergyChartPointProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceEnergyServiceTest {

    @Mock
    private DeviceEnergyHistoryRepository deviceEnergyHistoryRepository;

    @InjectMocks
    private DeviceEnergyService deviceEnergyService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                deviceEnergyService,
                "pricePerKwh",
                BigDecimal.valueOf(3.0)
        );
    }

    @Test
    void getMonthlyConsumption_shouldCalculateConsumedKwhAndCost() {
        UUID deviceId = UUID.randomUUID();
        YearMonth month = YearMonth.of(2026, 5);
        ZoneId zone = ZoneId.systemDefault();

        when(deviceEnergyHistoryRepository.calculateConsumptionWhByDeviceId(
                eq(deviceId),
                any(),
                any()
        )).thenReturn(BigDecimal.valueOf(5730));

        MonthlyEnergyResponse response =
                deviceEnergyService.getMonthlyConsumption(deviceId, month, zone);

        assertEquals("2026-05", response.month());
        assertEquals(BigDecimal.valueOf(5730), response.consumedWh());
        assertEquals(BigDecimal.valueOf(5.730).setScale(3), response.consumedKwh());
        assertEquals(BigDecimal.valueOf(17.19).setScale(2), response.cost());

        verify(deviceEnergyHistoryRepository)
                .calculateConsumptionWhByDeviceId(eq(deviceId), any(), any());
    }

    @Test
    void getMonthlyConsumption_shouldReturnZero_whenRepositoryReturnsNull() {
        UUID deviceId = UUID.randomUUID();
        YearMonth month = YearMonth.of(2026, 5);
        ZoneId zone = ZoneId.systemDefault();

        when(deviceEnergyHistoryRepository.calculateConsumptionWhByDeviceId(
                eq(deviceId),
                any(),
                any()
        )).thenReturn(null);

        MonthlyEnergyResponse response =
                deviceEnergyService.getMonthlyConsumption(deviceId, month, zone);

        assertEquals(BigDecimal.ZERO, response.consumedWh());
        assertEquals(BigDecimal.ZERO.setScale(3), response.consumedKwh());
        assertEquals(BigDecimal.ZERO.setScale(2), response.cost());
    }

    @Test
    void getMonthlyConsumptionByUser_shouldCalculateTotalUserConsumption() {
        UUID userId = UUID.randomUUID();
        YearMonth month = YearMonth.of(2026, 5);
        ZoneId zone = ZoneId.systemDefault();

        when(deviceEnergyHistoryRepository.calculateConsumptionWhByUserId(
                eq(userId),
                any(),
                any()
        )).thenReturn(BigDecimal.valueOf(2500));

        MonthlyEnergyResponse response =
                deviceEnergyService.getMonthlyConsumptionByUser(userId, month, zone);

        assertEquals("2026-05", response.month());
        assertEquals(BigDecimal.valueOf(2500), response.consumedWh());
        assertEquals(BigDecimal.valueOf(2.500).setScale(3), response.consumedKwh());
        assertEquals(BigDecimal.valueOf(7.50).setScale(2), response.cost());
    }

    @Test
    void getEnergyChartByUser_shouldReturnHourlyChart_whenPeriodIsDay() {
        UUID userId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 5, 1);
        ZoneId zone = ZoneId.systemDefault();

        EnergyChartPointProjection point = mock(EnergyChartPointProjection.class);
        when(point.getLabel()).thenReturn("10:00");
        when(point.getConsumedWh()).thenReturn(BigDecimal.valueOf(1200));

        when(deviceEnergyHistoryRepository.getUserHourlyEnergyChart(
                eq(userId),
                any(),
                any()
        )).thenReturn(List.of(point));

        List<EnergyChartPointResponse> result =
                deviceEnergyService.getEnergyChartByUser(userId, EnergyPeriod.DAY, date, zone);

        assertEquals(1, result.size());
        assertEquals("10:00", result.get(0).label());
        assertEquals(BigDecimal.valueOf(1200), result.get(0).consumedWh());
        assertEquals(BigDecimal.valueOf(1.200).setScale(3), result.get(0).consumedKwh());
        assertEquals(BigDecimal.valueOf(3.60).setScale(2), result.get(0).cost());

        verify(deviceEnergyHistoryRepository)
                .getUserHourlyEnergyChart(eq(userId), any(), any());
    }

    @Test
    void getEnergyChartByUser_shouldReturnDailyChart_whenPeriodIsWeek() {
        UUID userId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 5, 1);
        ZoneId zone = ZoneId.systemDefault();

        EnergyChartPointProjection point = mock(EnergyChartPointProjection.class);
        when(point.getLabel()).thenReturn("Monday");
        when(point.getConsumedWh()).thenReturn(BigDecimal.valueOf(3000));

        when(deviceEnergyHistoryRepository.getUserDailyEnergyChart(
                eq(userId),
                any(),
                any()
        )).thenReturn(List.of(point));

        List<EnergyChartPointResponse> result =
                deviceEnergyService.getEnergyChartByUser(userId, EnergyPeriod.WEEK, date, zone);

        assertEquals(1, result.size());
        assertEquals("Monday", result.get(0).label());
        assertEquals(BigDecimal.valueOf(3000), result.get(0).consumedWh());
        assertEquals(BigDecimal.valueOf(3.000).setScale(3), result.get(0).consumedKwh());
        assertEquals(BigDecimal.valueOf(9.00).setScale(2), result.get(0).cost());

        verify(deviceEnergyHistoryRepository)
                .getUserDailyEnergyChart(eq(userId), any(), any());
    }

    @Test
    void getEnergyChartByUser_shouldReturnDailyChart_whenPeriodIsMonth() {
        UUID userId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 5, 1);
        ZoneId zone = ZoneId.systemDefault();

        EnergyChartPointProjection point = mock(EnergyChartPointProjection.class);
        when(point.getLabel()).thenReturn("2026-05-01");
        when(point.getConsumedWh()).thenReturn(BigDecimal.valueOf(1500));

        when(deviceEnergyHistoryRepository.getUserDailyEnergyChart(
                eq(userId),
                any(),
                any()
        )).thenReturn(List.of(point));

        List<EnergyChartPointResponse> result =
                deviceEnergyService.getEnergyChartByUser(userId, EnergyPeriod.MONTH, date, zone);

        assertEquals(1, result.size());
        assertEquals("2026-05-01", result.get(0).label());
        assertEquals(BigDecimal.valueOf(1500), result.get(0).consumedWh());
        assertEquals(BigDecimal.valueOf(1.500).setScale(3), result.get(0).consumedKwh());
        assertEquals(BigDecimal.valueOf(4.50).setScale(2), result.get(0).cost());

        verify(deviceEnergyHistoryRepository)
                .getUserDailyEnergyChart(eq(userId), any(), any());
    }
}