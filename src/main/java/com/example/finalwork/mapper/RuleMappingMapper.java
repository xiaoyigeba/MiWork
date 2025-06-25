package com.example.finalwork.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RuleMappingMapper {
    @Select("Select rule_name from rules_mapping where rule_number = #{ruleNumber}")
    String getRuleNameByRuleNumber(Integer ruleNumber);
}
