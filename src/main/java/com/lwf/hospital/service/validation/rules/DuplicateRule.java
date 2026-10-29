package com.lwf.hospital.service.validation.rules;

import com.lwf.hospital.common.exception.BizException;
import com.lwf.hospital.service.validation.RegistrationContext;
import com.lwf.hospital.service.validation.RegistrationRule;

/**
 * 规则二：重复挂号校验。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class DuplicateRule implements RegistrationRule {

    @Override
    public String name() {
        return "DuplicateRule";
    }

    @Override
    public void check(RegistrationContext context) {
        if (context.existing() != null) {
            throw new BizException("你已经挂过该时段的号（排队号 " + context.existing().getQueueNo()
                    + "），无需重复挂号。");
        }
    }
}