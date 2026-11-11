package com.lwf.hospital.service;

import com.lwf.hospital.domain.dto.ScheduleView;

import java.time.LocalDate;
import java.util.List;

/**
 * 排班（号源）管理服务：新增、改容量、停诊/恢复、加号。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public interface ScheduleAdminService {

    /**
     * 日期区间内的排班（含停诊）。
     *
     * @param from         起始日期
     * @param to           结束日期
     * @param departmentId 科室 id，0 表示全部
     * @return 排班视图
     */
    List<ScheduleView> list(LocalDate from, LocalDate to, long departmentId);

    /**
     * 新增排班。
     *
     * @param doctorId   医生 id
     * @param workDate   出诊日期（不早于今天）
     * @param period     时段 AM/PM
     * @param quotaTotal 总号源
     * @return 新排班 id
     */
    long create(long doctorId, LocalDate workDate, String period, int quotaTotal);

    /**
     * 修改总号源（不得小于已挂号数）。
     *
     * @param scheduleId 排班 id
     * @param quotaTotal 新总号源
     */
    void updateQuota(long scheduleId, int quotaTotal);

    /**
     * 停诊。
     *
     * @param scheduleId 排班 id
     * @return 该排班下仍在候诊的患者人数（提示人工告知）
     */
    int close(long scheduleId);

    /**
     * 恢复开诊。
     *
     * @param scheduleId 排班 id
     */
    void open(long scheduleId);

    /**
     * 加号：在现有总量上再加若干，并记操作日志。
     *
     * @param scheduleId 排班 id
     * @param extra      增加数量
     * @param operatorId 操作人（管理员用户 id）
     */
    void addQuota(long scheduleId, int extra, long operatorId);
}