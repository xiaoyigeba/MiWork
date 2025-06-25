package com.example.finalwork.dto;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty; // 导入JsonProperty
import org.springframework.context.annotation.Bean;


@Data
public class WarningResponsePart {
    @JsonProperty("车架编号") // 映射 JSON 字段名 "车架编号" 到 Java 变量 "frameNumber"
    private Integer frameNumber;
    @JsonProperty("电池类型") // 映射 JSON 字段名 "电池类型" 到 Java 变量 "batteryType"
    private String batteryType;
    @JsonProperty("warnName") // 映射 JSON 字段名 "warnName" 到 Java 变量 "ruleName"
    private String ruleName;
    @JsonProperty("warnLevel") // 映射 JSON 字段名 "warnLevel" 到 Java 变量 "data"
    private Integer data; // 报警等级，-1 表示不报警
}
