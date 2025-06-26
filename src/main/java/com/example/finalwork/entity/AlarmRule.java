package com.example.finalwork.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class AlarmRule implements Serializable {
    private Integer rid;
    private Integer ruleNumber;
    private String batteryType;
    private String alarmRule; // Aviator 表达式字符串
    private Integer alarmLevel;
}