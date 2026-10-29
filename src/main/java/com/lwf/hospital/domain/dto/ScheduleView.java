package com.lwf.hospital.domain.dto;

import java.time.LocalDate;

/**
 * 排班视图：把排班、医生、科室三张表的字段拼成一行，供控制台表格与挂号选择使用。
 *
 * @param scheduleId  排班 id
 * @param doctorName  医生姓名
 * @param doctorTitle 职称
 * @param deptName    科室名称
 * @param workDate    出诊日期
 * @param period      时段（AM/PM）
 * @param quotaTotal  总号源
 * @param quotaUsed   已用号源
 * @param status      排班状态（OPEN/CLOSED）
 * @author LWF-123
 * @since 1.0.0
 */
public record ScheduleView(
        long scheduleId,
        String doctorName,
        String doctorTitle,
        String deptName,
        LocalDate workDate,
        String period,
        int quotaTotal,
        int quotaUsed,
        String status) {

    /**
     * 剩余号源。
     *
     * @return 剩余数量
     */
    public int remaining() {
        return Math.max(0, quotaTotal - quotaUsed);
    }

    /**
     * 中文时段。
     *
     * @return 上午/下午
     */
    public String periodText() {
        return "AM".equalsIgnoreCase(period) ? "上午" : "下午";
    }

    /**
     * 当前是否可挂号（用于状态列展示）。
     *
     * @return 可挂返回 true
     */
    public boolean available() {
        return "OPEN".equalsIgnoreCase(status) && remaining() > 0;
    }

    /**
     * 状态中文展示。
     *
     * @return 可挂 / 已满 / 已停诊
     */
    public String statusText() {
        if (!"OPEN".equalsIgnoreCase(status)) {
            return "已停诊";
        }
        return remaining() > 0 ? "可挂" : "已满";
    }
}