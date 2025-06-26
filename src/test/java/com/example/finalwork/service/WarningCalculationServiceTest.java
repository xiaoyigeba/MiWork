package com.example.finalwork.service;

import com.alibaba.fastjson2.JSON;
import com.example.finalwork.dto.SingleWarningRequestItem;
import com.example.finalwork.dto.WarningResponsePart;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WarningCalculationServiceTest {

    private WarningCalculationService warningCalculationService;
    private CarInfoService carInfoService;
    private AlarmRuleService alarmRuleService;
    private ObjectMapper objectMapper;
    private RuleMappingService ruleMappingService;

    @BeforeEach
    void setUp() {
        carInfoService = mock(CarInfoService.class);
        alarmRuleService = mock(AlarmRuleService.class);
        objectMapper = mock(ObjectMapper.class);
        ruleMappingService = mock(RuleMappingService.class);
        warningCalculationService = new WarningCalculationService(
                carInfoService,
                alarmRuleService,
                objectMapper,
                ruleMappingService
        );
        warningCalculationService = Mockito.spy(warningCalculationService);
    }

    @Test
    void testProcessMessage_ValidInput_ReturnsExpectedResult() {
        // Arrange: Prepare requestItems
        List<SingleWarningRequestItem> requestItems = new ArrayList<>();

        // Item 1
        SingleWarningRequestItem item1 = new SingleWarningRequestItem();
        item1.setFrameNumber(1001);
        item1.setRuleNumber(1);
        item1.setSignalData(JSON.toJSONString(Map.of("Mx", 12.0, "Mi", 0.6)));
        requestItems.add(item1);

        // Item 2
        SingleWarningRequestItem item2 = new SingleWarningRequestItem();
        item2.setFrameNumber(1002);
        item2.setRuleNumber(2);
        item2.setSignalData(JSON.toJSONString(Map.of("Ix", 12.0, "Ii", 11.7)));
        requestItems.add(item2);

        // Mock calculateWarnings behavior
        List<WarningResponsePart> mockResult1 = List.of(
                createResponsePart(1001, "三元电池", "电压差报警", 0)
        );

        List<WarningResponsePart> mockResult2 = List.of(
                createResponsePart(1002, "铁锂电池", "电流差报警", 2)
        );


        // We simulate that each call to calculateWarnings returns the corresponding result
        doReturn(mockResult1).when(warningCalculationService).calculateWarnings(1001, 1, item1.getSignalData());
        doReturn(mockResult2).when(warningCalculationService).calculateWarnings(1002, 2, item2.getSignalData());

        // Act
        List<WarningResponsePart> result = warningCalculationService.processMessage(requestItems);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify all items are as expected
        assertEquals(createResponsePart(1001, "三元电池", "电压差报警", 0), result.get(0));
        assertEquals(createResponsePart(1002, "铁锂电池", "电流差报警", 2), result.get(1));

        // Verify internal method was called
        verify(warningCalculationService, times(2)).calculateWarnings(anyInt(), any(), anyString());
    }

    @Test
    void testProcessMessage_WithNullItem_ShouldSkipGracefully() {
        // Arrange
        List<SingleWarningRequestItem> requestItems = new ArrayList<>();
        requestItems.add(null); // Add a null item

        // Act
        List<WarningResponsePart> result = warningCalculationService.processMessage(requestItems);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private WarningResponsePart createResponsePart(int frameNumber, String batteryType, String warnName, int warnLevel) {
        WarningResponsePart part = new WarningResponsePart();
        part.setFrameNumber(frameNumber);
        part.setBatteryType(batteryType);
        part.setRuleName(warnName);
        part.setData(warnLevel);
        return part;
    }
}