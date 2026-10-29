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
     * 某科室的医生。
     *
     * @param departmentId 科室 id
     * @return 医生列表
     */
    List<Doctor> findDoctorsByDepartment(long departmentId);

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
}