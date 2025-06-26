package com.example.finalwork.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RuleMappingMapper {
    @Select("SELECT rule_name AS ruleName FROM rules_mapping WHERE rule_number = #{ruleNumber}")
    String getRuleNameByRuleNumber(Integer ruleNumber);
}