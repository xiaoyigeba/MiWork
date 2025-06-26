package com.example.finalwork.controller;

import com.example.finalwork.dto.SingleWarningRequestItem;
import com.example.finalwork.dto.WarningResponse;
import com.example.finalwork.dto.WarningResponsePart;
import com.example.finalwork.service.WarningCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/warning")
public class WarningController {

    private final WarningCalculationService warningCalculationService;

    @Autowired
    public WarningController(WarningCalculationService warningCalculationService) {
        this.warningCalculationService = warningCalculationService;
    }

    /**
     * 根据新的 List<RequestItem> 结构计算预警等级。
     * <p>
     * 请求示例:
     * [
     * {
     * "carId": 1001,
     * "warnId": 1, // 可选
     * "signal": "{\"Mx\":1.0,\"Mi\":0.6}"
     * },
     * {
     * "carId": 1002,
     * "warnId": 2, // 可选
     * "signal": "{\"Ix\":12.0,\"Ii\":11.7}"
     * },
     * {
     * "carId": 1003,
     * "signal": "{\"Mx\":11.0,\"Mi\":9.6,\"Ix\":12.0,\"Ii\":11.7}"
     * }
     * ]
     * <p>
     * 返回示例:
     * {
     * "status": 200,
     * "message": "ok",
     * "data": [
     * {
     * "车架编号": 1,
     * "电池类型": "三元电池",
     * "warnName": "电压差报警",
     * "warnLevel": 0
     * },
     * {
     * "车架编号": 3,
     * "电池类型": "三元电池",
     * "warnName": "电压差报警",
     * "warnLevel": 2
     * },
     * {
     * "车架编号": 3,
     * "电池类型": "三元电池",
     * "warnName": "电流差报警",
     * "warnLevel": 2
     * }
     * ]
     * }
     */
    @PostMapping("/calculate")
    public ResponseEntity<WarningResponse> calculateWarning(@RequestBody List<SingleWarningRequestItem> requestItems) {
        WarningResponse response = new WarningResponse();
        List<WarningResponsePart> allTriggeredWarnings = new ArrayList<>(); // 收集所有请求项的报警结果

        // 参数校验
        if (requestItems == null || requestItems.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("请求体为空或无效。");
            response.setParts(new ArrayList<>()); // 返回空列表
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        for (SingleWarningRequestItem item : requestItems) {
            if (item == null || item.getFrameNumber() == null || item.getSignalData() == null || item.getSignalData().isEmpty()) {
                System.err.println("跳过无效的请求项：frameNumber 或 signal 为空。Item: " + item);
                continue; // Skip current invalid item, process next
            }

            try {
                // Call service layer for warning calculation.
                // It now returns a List<WarningResponsePart>
                List<WarningResponsePart> triggeredWarningsForItem = warningCalculationService.calculateWarnings(
                        item.getFrameNumber(),
                        item.getRuleNumber(), // Can be null
                        item.getSignalData()
                );

                // Add triggered warnings for the current item to the total list
                if (triggeredWarningsForItem != null && !triggeredWarningsForItem.isEmpty()) {
                    allTriggeredWarnings.addAll(triggeredWarningsForItem);
                }

            } catch (Exception e) {
                // Catch exceptions during single request item processing, log, but don't interrupt the whole request
                System.err.println("处理车架号 " + item.getFrameNumber() + " 的信号时发生异常: " + e.getMessage());
                e.printStackTrace();
            }
        }

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("ok");
        response.setParts(allTriggeredWarnings); // Set the aggregated list of all alarm results

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}