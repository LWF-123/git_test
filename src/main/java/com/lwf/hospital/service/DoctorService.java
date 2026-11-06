package com.lwf.hospital.service;

import com.lwf.hospital.domain.dto.DoctorView;

import java.util.List;

/**
 * 医生管理服务。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public interface DoctorService {

    /**
     * 全部医生（含停用）。
     *
     * @return 医生视图列表
     */
    List<DoctorView> listAll();

    /**
     * 某科室的医生（含停用）。
     *
     * @param departmentId 科室 id
     * @return 医生视图列表
     */
    List<DoctorView> listByDepartment(long departmentId);

    /**
     * 新增医生。
     *
     * @param doctorNo     工号（唯一）
     * @param name         姓名
     * @param title        职称
     * @param departmentId 科室 id
     * @return 新医生 id
     */
    long create(String doctorNo, String name, String title, long departmentId);

    /**
     * 修改医生。
     *
     * @param id           医生 id
     * @param doctorNo     工号
     * @param name         姓名
     * @param title        职称
     * @param departmentId 科室 id
     */
    void update(long id, String doctorNo, String name, String title, long departmentId);

    /**
     * 停用医生：置为停用并关闭其未来号源。
     *
     * @param id 医生 id
     * @return 被关闭的排班数量
     */
    int disable(long id);

    /**
     * 恢复医生。
     *
     * @param id 医生 id
     */
    void enable(long id);
}