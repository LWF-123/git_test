package com.lwf.hospital.service.validation;

import com.lwf.hospital.common.log.AppLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * 挂号规则链：按顺序执行规则，遇到第一个不通过就抛出异常。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class RegistrationRuleChain implements RegistrationRule {

    private final List<RegistrationRule> rules = new ArrayList<>();

    /**
     * 追加规则。
     *
     * @param rule 规则
     * @return 当前链，便于链式调用
     */
    public RegistrationRuleChain add(RegistrationRule rule) {
        rules.add(rule);
        return this;
    }

    @Override
    public String name() {
        return "RegistrationRuleChain";
    }

    @Override
    public void check(RegistrationContext context) {
        for (RegistrationRule rule : rules) {
            AppLogger.info("执行挂号规则：" + rule.name());
            rule.check(context);
        }
    }

    /**
     * 已注册的规则名称（启动时打印）。
     *
     * @return 规则名称列表
     */
    public List<String> ruleNames() {
        return rules.stream().map(RegistrationRule::name).toList();
    }
}