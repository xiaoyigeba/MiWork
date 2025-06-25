package com.example.finalwork.entity;

import lombok.Data;

@Data
public class AlarmRule {
    private Integer rid; // 序号，INT，主键，自增
    private Integer ruleNumber; // 规则编号，INT
    private String alarmRule; // 预警规则描述，VARCHAR(20)
    private String batteryType; // 电池类型，VARCHAR(20)
    private Integer alarmLevel; // 警报等级，INT
}
