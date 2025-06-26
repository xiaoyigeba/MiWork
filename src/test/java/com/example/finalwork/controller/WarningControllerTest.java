package com.example.finalwork.controller;

import com.example.finalwork.controller.WarningController;
import com.example.finalwork.dto.WarningResponsePart;
import com.example.finalwork.service.WarningCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WarningController.class)
public class WarningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WarningCalculationService warningCalculationService;

//    @InjectMocks
//    private WarningController warningController;

    private String inputJson;
    private List<WarningResponsePart> mockOutput;

    @BeforeEach
    void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(warningController).build();

        // 构造请求体 JSON
        inputJson = "[\n" +
                "  {\n" +
                "    \"carId\": 1001,\n" +
                "    \"warnId\": 1,\n" +
                "    \"signal\": \"{\\\"Mx\\\":12.0,\\\"Mi\\\":0.6}\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"carId\": 1002,\n" +
                "    \"warnId\": 2,\n" +
                "    \"signal\": \"{\\\"Ix\\\":12.0,\\\"Ii\\\":11.7}\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"carId\": 1003,\n" +
                "    \"signal\": \"{\\\"Mx\\\":11.0,\\\"Mi\\\":9.6,\\\"Ix\\\":12.0,\\\"Ii\\\":11.7}\"\n" +
                "  }\n" +
                "]";

        // 构造模拟返回值
        mockOutput = List.of(
                createResponsePart(1001, "三元电池", "电压差报警", 0),
                createResponsePart(1002, "铁锂电池", "电流差报警", 2),
                createResponsePart(1003, "三元电池", "电压差报警", 2),
                createResponsePart(1003, "三元电池", "电流差报警", 2)
        );
    }

    private WarningResponsePart createResponsePart(int frameNumber, String batteryType, String ruleName, int data) {
        WarningResponsePart part = new WarningResponsePart();
        part.setFrameNumber(frameNumber);
        part.setBatteryType(batteryType);
        part.setRuleName(ruleName);
        part.setData(data);
        return part;
    }

    @Test
    void testCalculateWarning_Successful() throws Exception {
        when(warningCalculationService.processMessage(anyList())).thenReturn(mockOutput);

        mockMvc.perform(post("/api/warning/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].车架编号").value(1001))
                .andExpect(jsonPath("$.data[0].warnName").value("电压差报警"))
                .andExpect(jsonPath("$.data[1].电池类型").value("铁锂电池"))
                .andExpect(jsonPath("$.data[2].warnLevel").value(2));
    }
}