package com.example.finalwork.mapper;

import com.example.finalwork.entity.AlarmRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param; // 导入 @Param
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AlarmRuleMapper {
    //根据电池类型和可选的规则编号查询规则
    @Select({
            "<script>",
            "SELECT rid, rule_number as ruleNumber, battery_type as batteryType, alarm_rule as alarmRule, alarm_level as alarmLevel FROM alarm_rules",
            "WHERE battery_type = #{batteryType}",
            "<if test='ruleNumber != null'>",
            "AND rule_number = #{ruleNumber}",
            "</if>",
            "</script>"
    })
    List<AlarmRule> findAlarmRulesByBatteryTypeAndOptionalRuleNumber(@Param("batteryType") String batteryType,
                                                                     @Param("ruleNumber") Integer ruleNumber);

    //    @Select({
//            "<script>",
//            "SELECT rid, rule_number , battery_type , alarm_rule , alarm_level FROM alarm_rules",
//            "WHERE battery_type = #{batteryType}",
//            "<if test='ruleNumber != null'>",
//            "AND rule_number = #{ruleNumber}",
//            "</if>",
//            "</script>"
//    })
//    List<AlarmRule> findAlarmRulesByBatteryTypeAndOptionalRuleNumber(@Param("batteryType") String batteryType,
//                                                                     @Param("ruleNumber") Integer ruleNumber);
    @Select({
            "<script>",
            "SELECT alarm_rule FROM alarm_rules",
            "WHERE battery_type = #{batteryType}",
            "<if test='ruleNumber != null'>",
            "AND rule_number = #{ruleNumber}",
            "</if>",
            "</script>"
    })
    List<AlarmRule> findAlarmRulesByBatteryTypeAndOptionalRuleNumber1(@Param("batteryType") String batteryType,
                                                                      @Param("ruleNumber") Integer ruleNumber);

    // 查询所有规则 (保留，可能有用)
    @Select("SELECT rid, rule_number, name, battery_type, alarm_rule, alarm_level FROM alarm_rules")
    List<AlarmRule> findAllAlarmRules();
}