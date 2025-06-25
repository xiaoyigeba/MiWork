package com.example.finalwork.controller;

import com.example.finalwork.dto.SingleWarningRequestItem; // 导入新的请求项 DTO
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
     *
     * 请求示例:
     * [
     * {
     * "carId": 1,
     * "warnId": 1001, // 可选
     * "signal": "{\"Mx\":1.0,\"Mi\":0.6}"
     * },
     * {
     * "carId": 2,
     * "warnId": 1002, // 可选
     * "signal": "{\"Ix\":12.0,\"Ii\":11.7}"
     * },
     * {
     * "carId": 3,
     * "signal": "{\"Mx\":11.0,\"Mi\":9.6,\"Ix\":12.0,\"Ii\":11.7}"
     * }
     * ]
     *
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
                // 对于单个无效项，可以记录日志或跳过，不影响其他项
                System.err.println("跳过无效的请求项：frameNumber 或 signal 为空。Item: " + item);
                continue; // 跳过当前项，处理下一项
            }

            try {
                // 调用服务层进行警告计算
                WarningResponsePart triggeredWarningsForItem = warningCalculationService.calculateWarnings(
                        item.getFrameNumber(),
                        item.getRuleNumber(), // ruleNumber 可能是 null，服务层会处理
                        item.getSignalData()
                );

                // 将当前项触发的警告添加到总列表中
                if (triggeredWarningsForItem != null) {
                    allTriggeredWarnings.add(triggeredWarningsForItem);
                }

            } catch (Exception e) {
                // 捕获单个请求项处理时的异常，记录日志，但不中断整个请求
                System.err.println("处理车架号 " + item.getFrameNumber() + " 的信号时发生异常: " + e.getMessage());
                e.printStackTrace();
                // 可以在这里选择是否将此错误信息包含在响应中，例如添加到 message 或单独的错误列表
            }
        }

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("ok");
        response.setParts(allTriggeredWarnings); // 设置所有报警结果的聚合列表

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}