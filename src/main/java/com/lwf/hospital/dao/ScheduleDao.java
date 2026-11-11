package com.lwf.hospital.dao;

import com.lwf.hospital.domain.dto.ScheduleView;
import com.lwf.hospital.domain.entity.Department;
import com.lwf.hospital.domain.entity.Doctor;
import com.lwf.hospital.domain.entity.DoctorSchedule;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 科室 / 医生 / 排班数据访问接口。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public interface ScheduleDao {

    /**
     * 全部科室。
     *
     * @return 科室列表
     */
    List<Department> findDepartments();

    /**
     * 某科室的在职医生。
     *
     * @param departmentId 科室 id
     * @return 医生列表
     */
    List<Doctor> findDoctorsByDepartment(long departmentId);

    /**
     * 事务内关闭某医生今天及以后的可挂号源（医生停用时联动）。
     *
     * @param connection 事务连接
     * @param doctorId   医生 id
     * @return 被关闭的排班数量
     */
    int closeFutureSchedules(Connection connection, long doctorId);

    /**
     * 查询今天及以后仍可挂号的排班。
     *
     * @param departmentId 科室 id，传 0 表示全部
     * @return 排班视图
     */
    List<ScheduleView> findAvailable(long departmentId);

    /**
     * 某医生某天的排班（医生端选班用）。
     *
     * @param doctorId 医生 id
     * @param date     日期
     * @return 排班视图
     */
    List<ScheduleView> findByDoctorAndDate(long doctorId, LocalDate date);

    /**
     * 事务内加行锁读取排班（{@code SELECT ... FOR UPDATE}）。
     *
     * @param connection 事务连接
     * @param scheduleId 排班 id
     * @return 排班
     */
    Optional<DoctorSchedule> lockForUpdate(Connection connection, long scheduleId);

    /**
     * 事务内尝试占用一个号源。
     *
     * @param connection 事务连接
     * @param scheduleId 排班 id
     * @return 1=占用成功，0=号源已满
     */
    int tryIncreaseQuota(Connection connection, long scheduleId);

    /**
     * 日期区间内的排班（管理员维护用，含已停诊）。
     *
     * @param from         起始日期（含）
     * @param to           结束日期（含）
     * @param departmentId 科室 id，0 表示全部
     * @return 排班视图
     */
    List<ScheduleView> findByDateRange(LocalDate from, LocalDate to, long departmentId);

    /**
     * 按 id 查询排班。
     *
     * @param scheduleId 排班 id
     * @return 排班
     */
    Optional<DoctorSchedule> findById(long scheduleId);

    /**
     * 查某医生某天某时段的排班（唯一性校验）。
     *
     * @param doctorId 医生 id
     * @param workDate 日期
     * @param period   时段
     * @return 排班
     */
    Optional<DoctorSchedule> findByDoctorDatePeriod(long doctorId, LocalDate workDate, String period);

    /**
     * 新增排班。
     *
     * @param doctorId  医生 id
     * @param workDate  日期
     * @param period    时段
     * @param quotaTotal 总号源
     * @return 新排班 id
     */
    long insert(long doctorId, LocalDate workDate, String period, int quotaTotal);

    /**
     * 修改总号源（仅当新容量不小于已挂号数时才生效）。
     *
     * @param scheduleId 排班 id
     * @param quotaTotal 新总号源
     * @return 受影响行数
     */
    int updateQuotaTotal(long scheduleId, int quotaTotal);

    /**
     * 加号：总号源增加若干。
     *
     * @param scheduleId 排班 id
     * @param extra      增加数量
     * @return 受影响行数
     */
    int addQuotaTotal(long scheduleId, int extra);

    /**
     * 修改排班状态。
     *
     * @param scheduleId 排班 id
     * @param status     OPEN / CLOSED
     * @return 受影响行数
     */
    int updateStatus(long scheduleId, String status);
}