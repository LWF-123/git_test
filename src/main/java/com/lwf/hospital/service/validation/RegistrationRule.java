package com.lwf.hospital.service.validation;

/**
 * 挂号规则：责任链中的一个节点，新增规则只需实现本接口并加入链。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public interface RegistrationRule {

    /**
     * 规则名称。
     *
     * @return 名称
     */
    String name();

    /**
     * 校验，不通过时抛出业务异常。
     *
     * @param context 校验上下文
     */
    void check(RegistrationContext context);
}