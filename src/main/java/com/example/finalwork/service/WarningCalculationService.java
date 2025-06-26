package com.example.finalwork.service;

import com.example.finalwork.dto.WarningResponsePart;
import com.example.finalwork.entity.AlarmRule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WarningCalculationService {

    private final CarInfoService carInfoService;
    private final AlarmRuleService alarmRuleService;
    private final ObjectMapper objectMapper;
    private final RuleMappingService ruleMappingService;
    private final RedisTemplate<String, Object> redisTemplate;

    // Cache compiled Aviator expressions to avoid recompilation and improve performance
    private final Map<String, Expression> compiledExpressionCache = new ConcurrentHashMap<>();

    /**
     * Calculates warning levels for a given vehicle.
     *
     * @param frameNumber    Vehicle frame number.
     * @param ruleNumber     Optional rule number. If null, all relevant rules are applied.
     * @param signalDataJson Signal data in JSON string format (e.g., "{\"Mx\":1.0,\"Mi\":0.6}").
     * @return A list of WarningResponsePart, each representing a triggered warning.
     * Returns an empty list if no warnings are triggered or if an error occurs.
     */
    public List<WarningResponsePart> calculateWarnings(Integer frameNumber, Integer ruleNumber, String signalDataJson) {
        List<WarningResponsePart> triggeredWarnings = new ArrayList<>();

        // 1. Get battery type
        String batteryType = carInfoService.getBatteryTypeByFrameNumber(frameNumber);

        if (batteryType == null) {
            System.err.println("未找到车架号 " + frameNumber + " 的电池类型。");
            // If battery type not found, we cannot proceed with rules, return empty list
            return Collections.emptyList();
        }

        // 2. Get alarm rules
        List<AlarmRule> rules = alarmRuleService.getAlarmRules(batteryType, ruleNumber);


        if (rules == null || rules.isEmpty()) {
            System.out.println("未找到电池类型 " + batteryType + (ruleNumber != null ? " 和规则编号 " + ruleNumber : "") + " 的预警规则。");
            return Collections.emptyList(); // No matching rules, return empty list
        }

        // 3. Parse signal data
        Map<String, Object> signalDataMap = null;
        try {
            // Use TypeReference to ensure parsing as Map<String, Object>
            signalDataMap = objectMapper.readValue(signalDataJson, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            System.err.println("解析信号数据 JSON 失败: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList(); // JSON parsing failed
        }

        // 4. Evaluate rules and determine alarm levels
        for (AlarmRule rule : rules) {
            try {
                // Remove "$" symbol from rules, Aviator does not support it
                String aviatorExpression = rule.getAlarmRule().replace("$", "");
                Expression expression = compiledExpressionCache.computeIfAbsent(aviatorExpression, AviatorEvaluator::compile);

                // Evaluate the expression
                Boolean isAlarm = (Boolean) expression.execute(signalDataMap);

                if (isAlarm != null && isAlarm) {
                    WarningResponsePart part = new WarningResponsePart();
                    part.setFrameNumber(frameNumber);
                    part.setBatteryType(batteryType);
                    // Get rule name using the rule number from the current rule
                    String ruleName = ruleMappingService.getRuleNameByRuleNumber(rule.getRuleNumber());
                    part.setRuleName(ruleName);
                    part.setData(rule.getAlarmLevel()); // Set the alarm level from the rule
                    triggeredWarnings.add(part);

                    // If a specific ruleNumber was provided, we only need to find one match for that rule.
                    // If ruleNumber is null, we need to evaluate all rules and collect all triggered ones.
                    if (ruleNumber != null) {
                        break; // If a specific rule was requested and found, no need to check other rules
                    }
                }

            } catch (Exception e) {
                System.err.println("评估规则失败 (Rule ID: " + rule.getRid() + e.getMessage());
                e.printStackTrace();
                // Even if one rule evaluation fails, try to continue evaluating other rules
            }
        }
        return triggeredWarnings;
    }
}