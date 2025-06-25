package com.example.finalwork.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WarningResponse {
    private Integer status; // 状态码，如 200
    private String message;
    @JsonProperty("data")// 信息，"ok" 或具体报错信息
    private List<WarningResponsePart> parts;   // 报警等级，-1 表示不报警
}