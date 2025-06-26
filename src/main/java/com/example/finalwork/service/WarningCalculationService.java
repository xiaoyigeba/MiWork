package com.example.finalwork.service;

import com.example.finalwork.dto.WarningResponsePart;
import com.example.finalwork.entity.AlarmRule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WarningCalculationService {

    private final CarInfoService carInfoService;
    private final AlarmRuleService alarmRuleService;
    private final ObjectMapper objectMapper;
    private  final RuleMappingService ruleMappingService;


    // 缓存编译后的 Aviator 表达式，避免重复编译，提高性能
    private final Map<String, Expression> compiledExpressionCache = new ConcurrentHashMap<>();

    @Autowired
    public WarningCalculationService(CarInfoService carInfoService,
                                     AlarmRuleService alarmRuleService,
                                     ObjectMapper objectMapper,
                                     RuleMappingService ruleMappingService) {
        this.carInfoService = carInfoService;
        this.alarmRuleService = alarmRuleService;
        this.objectMapper = objectMapper;
        this.ruleMappingService = ruleMappingService;

    }

    /**
     * 计算指定车辆的预警等级。
     *
     * @param frameNumber 车架编号
     * @param ruleNumber 可选的规则编号。如果为 null，则应用所有相关规则。
     * @param signalDataJson 信号数据，JSON 字符串形式 (例如: "{\"Mx\":1.0,\"Mi\":0.6}")
     * @return 报警等级 (-1 为不报警，0-4 为报警等级)，如果发生错误则返回 -2 或抛出异常
     */
    public WarningResponsePart calculateWarnings(Integer frameNumber, Integer ruleNumber, String signalDataJson) {
        // 1. 获取电池类型
//        CarInfo carInfo = carInfoService.getCarInfoByFrameNumber(frameNumber);
//        if (carInfo == null || carInfo.getBatteryType() == null) {
//            System.err.println("未找到车架号 " + frameNumber + " 的车辆信息或电池类型。");
//            return -2; // 表示错误或无法处理
//        }
//        String batteryType = carInfo.getBatteryType();
        WarningResponsePart warningResponsePart = new WarningResponsePart();
        String batteryType = carInfoService.getBatteryTypeByFrameNumber(frameNumber);

        if (batteryType == null) {
            System.err.println("未找到车架号 " + frameNumber + " 的电池类型。");
            warningResponsePart.setData(-2); // 表示错误或无法处理
        }
        String ruleName = ruleMappingService.getRuleNameByRuleNumber(ruleNumber);

        // 2. 获取预警规则
        List<AlarmRule> rules = alarmRuleService.getAlarmRules(batteryType, ruleNumber);
        List<String> AlarmRules = rules.stream().map(AlarmRule::getAlarmRule).toList();
        if (rules == null || rules.isEmpty()) {
            System.out.println("未找到电池类型 " + batteryType + (ruleNumber != null ? " 和规则编号 " + ruleNumber : "") + " 的预警规则。");
            warningResponsePart.setData(-2); // 没有匹配的规则，视为不报警
        }

        // 3. 解析信号数据
        Map<String, Object> signalDataMap = null;
        try {
            // 使用 TypeReference 来确保解析为 Map<String, Object>
            signalDataMap = objectMapper.readValue(signalDataJson, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            System.err.println("解析信号数据 JSON 失败: " + e.getMessage());
            e.printStackTrace();
            warningResponsePart.setData(-2); // JSON 解析失败
        }

        // 4. 评估规则并确定最高报警等级
        int highestWarnLevel = -1; // 0级最高，-1表示不报警

        for (AlarmRule rule : rules) {
            try {
                // 移除规则中的 "$" 符号，Aviator 不支持
                String aviatorExpression = rule.getAlarmRule().replace("$", "");
                Expression expression = compiledExpressionCache.computeIfAbsent(aviatorExpression, AviatorEvaluator::compile);

                // 评估表达式
                Boolean isAlarm = (Boolean) expression.execute(signalDataMap);

                if (isAlarm != null && isAlarm) {
                warningResponsePart.setData(highestWarnLevel);
                }
            } catch (Exception e) {
                System.err.println("评估规则失败: " + e.getMessage());
                e.printStackTrace();
                // 即使某个规则评估失败，也尝试继续评估其他规则，不中断整个流程
            }
        }
        warningResponsePart.setBatteryType(batteryType);
        warningResponsePart.setRuleName(ruleName);
        warningResponsePart.setFrameNumber(frameNumber);
        return warningResponsePart;
    }
}