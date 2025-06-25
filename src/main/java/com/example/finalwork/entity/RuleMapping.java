package com.example.finalwork.entity;

import lombok.Data;

@Data
public class RuleMapping {
    private Integer ruleNumber; // 规则编号，INT，主键
    private String ruleName; // 名称，VARCHAR(50)
}
