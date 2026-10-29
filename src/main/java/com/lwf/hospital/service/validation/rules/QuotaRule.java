package com.lwf.hospital.service.validation.rules;

import com.lwf.hospital.common.exception.BizException;
import com.lwf.hospital.domain.entity.DoctorSchedule;
import com.lwf.hospital.service.validation.RegistrationContext;
import com.lwf.hospital.service.validation.RegistrationRule;

/**
 * 规则一：号源校验（提前拦截，真正的并发安全由条件更新保证）。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class QuotaRule implements RegistrationRule {

    @Override
    public String name() {
        return "QuotaRule";
    }

    @Override
    public void check(RegistrationContext context) {
        DoctorSchedule schedule = context.schedule();
        if (schedule.remaining() <= 0) {
            throw new BizException("号源已满：该时段已挂 " + schedule.getQuotaUsed() + "/"
                    + schedule.getQuotaTotal() + "，请选择其他医生或时段。");
        }
    }
}