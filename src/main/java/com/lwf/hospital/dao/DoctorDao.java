package com.lwf.hospital.dao;

import com.lwf.hospital.domain.dto.DoctorView;
import com.lwf.hospital.domain.entity.Doctor;

import java.util.List;
import java.util.Optional;

/**
 * 医生数据访问接口。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public interface DoctorDao {

    /**
     * 全部医生（含科室名与状态）。
     *
     * @return 医生视图列表
     */
    List<DoctorView> findAll();

    /**
     * 某科室的医生（含停用）。
     *
     * @param departmentId 科室 id
     * @return 医生视图列表
     */
    List<DoctorView> findByDepartment(long departmentId);

    /**
     * 按 id 查询医生实体。
     *
     * @param id 医生 id
     * @return 医生
     */
    Optional<Doctor> findById(long id);

    /**
     * 按工号查询（唯一性校验）。
     *
     * @param doctorNo 工号
     * @return 医生
     */
    Optional<Doctor> findByNo(String doctorNo);

    /**
     * 新增医生。
     *
     * @param doctorNo     工号
     * @param name         姓名
     * @param title        职称
     * @param departmentId 科室 id
     * @return 新医生 id
     */
    long insert(String doctorNo, String name, String title, long departmentId);

    /**
     * 修改医生。
     *
     * @param id           医生 id
     * @param doctorNo     工号
     * @param name         姓名
     * @param title        职称
     * @param departmentId 科室 id
     * @return 受影响行数
     */
    int update(long id, String doctorNo, String name, String title, long departmentId);

    /**
     * 设置在职状态。
     *
     * @param id      医生 id
     * @param enabled 是否在职
     * @return 受影响行数
     */
    int setEnabled(long id, boolean enabled);
}