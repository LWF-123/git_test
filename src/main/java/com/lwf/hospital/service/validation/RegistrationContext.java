package com.lwf.hospital.service.validation;

import com.lwf.hospital.domain.entity.DoctorSchedule;
import com.lwf.hospital.domain.entity.Registration;
import com.lwf.hospital.domain.enums.RegistrationType;

/**
 * 挂号校验上下文：一次校验需要的全部数据都在这里，规则只读不查库，便于单测。
 *
 * @param patientId 患者 id
 * @param schedule  目标排班
 * @param existing  该患者在该排班下已有的有效挂号，可能为 null
 * @param type      挂号类型
 * @author LWF-123
 * @since 1.0.0
 */
public record RegistrationContext(
        long patientId,
        DoctorSchedule schedule,
        Registration existing,
        RegistrationType type) {
}