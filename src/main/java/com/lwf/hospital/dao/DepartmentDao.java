package com.lwf.hospital.dao;

import com.lwf.hospital.domain.entity.Department;

import java.util.List;
import java.util.Optional;

/**
 * 科室数据访问接口。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public interface DepartmentDao {

    /**
     * 全部科室。
     *
     * @return 科室列表
     */
    List<Department> findAll();

    /**
     * 按 id 查询。
     *
     * @param id 科室 id
     * @return 科室
     */
    Optional<Department> findById(long id);

    /**
     * 按编码查询（用于唯一性校验）。
     *
     * @param deptCode 科室编码
     * @return 科室
     */
    Optional<Department> findByCode(String deptCode);

    /**
     * 新增科室。
     *
     * @param deptCode 编码
     * @param name     名称
     * @param location 位置
     * @return 新科室 id
     */
    long insert(String deptCode, String name, String location);

    /**
     * 修改科室。
     *
     * @param id       科室 id
     * @param deptCode 编码
     * @param name     名称
     * @param location 位置
     * @return 受影响行数
     */
    int update(long id, String deptCode, String name, String location);

    /**
     * 删除科室。
     *
     * @param id 科室 id
     * @return 受影响行数
     */
    int delete(long id);

    /**
     * 统计该科室下的医生数量（含停用），用于删除前校验。
     *
     * @param departmentId 科室 id
     * @return 医生数量
     */
    int countDoctors(long departmentId);
}