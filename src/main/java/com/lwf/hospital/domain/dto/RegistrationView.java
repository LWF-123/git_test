package com.lwf.hospital.domain.dto;

import com.lwf.hospital.domain.enums.RegistrationType;
import com.lwf.hospital.domain.enums.TicketStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 挂号/队列视图：供“我的挂号”“候诊队列”“叫号结果”共用。
 *
 * @param registrationId 挂号单 id
 * @param patientName    患者姓名
 * @param deptName       科室
 * @param doctorName     医生
 * @param workDate       出诊日期
 * @param period         时段
 * @param type           挂号类型
 * @param queueNo        排队号
 * @param status         当前状态
 * @param fee            挂号费
 * @author LWF-123
 * @since 1.0.0
 */
public record RegistrationView(
        long registrationId,
        String patientName,
        String deptName,
        String doctorName,
        LocalDate workDate,
        String period,
        RegistrationType type,
        int queueNo,
        TicketStatus status,
        BigDecimal fee) {

    /**
     * 中文时段。
     *
     * @return 上午/下午
     */
    public String periodText() {
        return "AM".equalsIgnoreCase(period) ? "上午" : "下午";
    }
}