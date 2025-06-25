package com.example.finalwork.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonProperty; // 导入JsonProperty

@Data
public class SingleWarningRequestItem {
    @JsonProperty("carId") // 映射 JSON 字段名 "carId" 到 Java 变量 "frameNumber"
    private Integer frameNumber;

    @JsonProperty("warnId") // 映射 JSON 字段名 "warnId" 到 Java 变量 "ruleNumber"
    private Integer ruleNumber; // 可选的规则编号

    @JsonRawValue
    @JsonProperty("signal") // 映射 JSON 字段名 "signal" 到 Java 变量 "signalData"
    private String signalData; // 信号信息 JSON 字符串
}