package com.lwf.hospital.service;

import com.lwf.hospital.domain.entity.Department;

import java.util.List;

/**
 * 科室管理服务。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public interface DepartmentService {

    /**
     * 全部科室。
     *
     * @return 科室列表
     */
    List<Department> listAll();

    /**
     * 新增科室。
     *
     * @param deptCode 科室编码（唯一）
     * @param name     科室名称
     * @param location 位置
     * @return 新科室 id
     */
    long create(String deptCode, String name, String location);

    /**
     * 修改科室。
     *
     * @param id       科室 id
     * @param deptCode 科室编码
     * @param name     科室名称
     * @param location 位置
     */
    void update(long id, String deptCode, String name, String location);

    /**
     * 删除科室（存在医生时拒绝）。
     *
     * @param id 科室 id
     */
    void delete(long id);
}