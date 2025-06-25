package com.example.finalwork.service.impl;

import com.example.finalwork.mapper.RuleMappingMapper;
import com.example.finalwork.service.RuleMappingService;
import org.springframework.stereotype.Service;

@Service
public class RuleMappingServiceImpl implements RuleMappingService {
    private final RuleMappingMapper RuleMappingMapper;

    public RuleMappingServiceImpl(RuleMappingMapper ruleMappingMapper) {
        RuleMappingMapper = ruleMappingMapper;
    }

    @Override
    public String getRuleNameByRuleNumber(Integer ruleNumber) {
        // 实现根据规则编号获取规则名称的逻辑
        // 这里可以根据具体需求从数据库或其他数据源获取规则名称
        // 示例代码中直接返回一个固定的规则名称
        return RuleMappingMapper.getRuleNameByRuleNumber(ruleNumber);
    }
}
