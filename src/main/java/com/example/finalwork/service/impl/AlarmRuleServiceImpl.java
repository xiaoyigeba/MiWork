package com.example.finalwork.service.impl;

import com.example.finalwork.entity.AlarmRule;
import com.example.finalwork.mapper.AlarmRuleMapper;
import com.example.finalwork.service.AlarmRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class AlarmRuleServiceImpl implements AlarmRuleService {

    @Autowired
    private final AlarmRuleMapper alarmRuleMapper;
    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_KEY_ALL_ALARM_RULES = "alarm_rules:all";
    private static final String CACHE_KEY_PREFIX_ALARM_RULE = "alarm_rules:"; // 统一前缀

    @Autowired
    public AlarmRuleServiceImpl(AlarmRuleMapper alarmRuleMapper, RedisTemplate<String, Object> redisTemplate) {
        this.alarmRuleMapper = alarmRuleMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<AlarmRule> getAlarmRules(String batteryType, Integer ruleNumber) {
        String cacheKey = CACHE_KEY_PREFIX_ALARM_RULE + batteryType + (ruleNumber != null ? ":" + ruleNumber : ":all");
        // 从 Redis 列表获取规则
        List<AlarmRule> rules = getList(cacheKey);

        if (!rules.isEmpty()) {
            System.out.println("从 Redis 缓存获取预警规则: " + cacheKey);
            return rules;
        }

        System.out.println("缓存未命中，从数据库查询预警规则: " + cacheKey);
        rules = alarmRuleMapper.findAlarmRulesByBatteryTypeAndOptionalRuleNumber(batteryType, ruleNumber);
        if (!rules.isEmpty()) {
            // 将规则存入 Redis 列表
            redisTemplate.opsForList().rightPushAll(cacheKey, rules.toArray());
            // 设置过期时间
            redisTemplate.expire(cacheKey, 10, TimeUnit.MINUTES);
        }
        return rules;
    }

    @Override
    public List<AlarmRule> getAllAlarmRules() {
        return getAlarmRules("all", null);
    }

    /**
     * 从 Redis 列表中获取 AlarmRule 列表
     *
     * @param key Redis 键
     * @return AlarmRule 列表
     */
    private List<AlarmRule> getList(String key) {
        List<Object> objectList = redisTemplate.opsForList().range(key, 0, -1);
        if (objectList == null) {
            return Collections.emptyList();
        }
        return objectList.stream()
                .filter(obj -> obj instanceof AlarmRule)
                .map(obj -> (AlarmRule) obj)
                .collect(Collectors.toList());
    }
}


//package com.example.finalwork.service.impl;
//
//import com.example.finalwork.entity.AlarmRule;
//import com.example.finalwork.mapper.AlarmRuleMapper;
//import com.example.finalwork.service.AlarmRuleService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//@Service
//public class AlarmRuleServiceImpl implements AlarmRuleService {
//
//    private final AlarmRuleMapper alarmRuleMapper;
//    private final RedisTemplate<String, Object> redisTemplate;
//    private static final String CACHE_KEY_ALL_ALARM_RULES = "alarm_rules:all";
//    private static final String CACHE_KEY_PREFIX_ALARM_RULE = "alarm_rules:"; // 统一前缀
//
//    @Autowired
//    public AlarmRuleServiceImpl(AlarmRuleMapper alarmRuleMapper, RedisTemplate<String, Object> redisTemplate) {
//        this.alarmRuleMapper = alarmRuleMapper;
//        this.redisTemplate = redisTemplate;
//    }
//
//    @Override
//    public List<AlarmRule> getAlarmRules(String batteryType, Integer ruleNumber) {
//        String cacheKey = CACHE_KEY_PREFIX_ALARM_RULE + batteryType + (ruleNumber != null ? ":" + ruleNumber : ":all");
//        List<AlarmRule> rules = (List<AlarmRule>) redisTemplate.opsForValue().get(cacheKey);
//
//        if (rules != null && !rules.isEmpty()) {
//            System.out.println("从 Redis 缓存获取预警规则: " + cacheKey);
//            return rules;
//        }
//
//        System.out.println("缓存未命中，从数据库查询预警规则: " + cacheKey);
//        rules = alarmRuleMapper.findAlarmRulesByBatteryTypeAndOptionalRuleNumber(batteryType, ruleNumber);
//        if (rules != null && !rules.isEmpty()) {
//            redisTemplate.opsForValue().set(cacheKey, rules, 10, TimeUnit.MINUTES); // 缓存 60 分钟
//        }
//        return rules;
//    }
//
//    @Override
//    public List<AlarmRule> getAllAlarmRules() {
//        return getAlarmRules("all", null); // 可以复用上面的方法，但通常直接查所有不需要 batteryType
//        // 或者直接调用 mapper.findAllAlarmRules() 如果你想保持独立逻辑
//        // List<AlarmRule> rules = (List<AlarmRule>) redisTemplate.opsForValue().get(CACHE_KEY_ALL_ALARM_RULES);
//        // if (rules != null && !rules.isEmpty()) {
//        //     return rules;
//        // }
//        // rules = alarmRuleMapper.findAllAlarmRules();
//        // if (rules != null && !rules.isEmpty()) {
//        //     redisTemplate.opsForValue().set(CACHE_KEY_ALL_ALARM_RULES, rules, 60, TimeUnit.MINUTES);
//        // }
//        // return rules;
//    }
//}