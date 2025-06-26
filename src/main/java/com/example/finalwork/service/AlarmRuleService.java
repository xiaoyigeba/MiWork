package com.example.finalwork.service;

import com.example.finalwork.entity.AlarmRule;

import java.util.List;


public interface AlarmRuleService {
    List<AlarmRule> getAlarmRules(String batteryType, Integer ruleNumber); // 新增方法

    List<AlarmRule> getAllAlarmRules(); // 保留
}