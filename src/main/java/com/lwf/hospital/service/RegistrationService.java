package com.lwf.hospital.service;

import com.lwf.hospital.domain.dto.RegistrationView;
import com.lwf.hospital.domain.dto.ScheduleView;
import com.lwf.hospital.domain.entity.Department;
import com.lwf.hospital.domain.entity.Doctor;
import com.lwf.hospital.domain.enums.RegistrationType;

import java.util.List;

/**
 * 挂号服务：查科室/医生/排班、挂号、查我的挂号。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public interface RegistrationService {

    /**
     * 全部科室。
     *
     * @return 科室列表
     */
    List<Department> listDepartments();

    /**
     * 某科室的医生。
     *
     * @param departmentId 科室 id
     * @return 医生列表
     */
    List<Doctor> listDoctors(long departmentId);

    /**
     * 可挂号源。
     *
     * @param departmentId 科室 id，传 0 表示全部
     * @return 排班视图
     */
    List<ScheduleView> listAvailableSchedules(long departmentId);

    /**
     * 挂号。
     *
     * @param patientId  患者 id
     * @param scheduleId 排班 id
     * @param type       挂号类型
     * @return 挂号成功后的挂号单视图
     */
    RegistrationView register(long patientId, long scheduleId, RegistrationType type);

    /**
     * 我的挂号记录。
     *
     * @param patientId 患者 id
     * @return 挂号记录
     */
    List<RegistrationView> myRegistrations(long patientId);
}