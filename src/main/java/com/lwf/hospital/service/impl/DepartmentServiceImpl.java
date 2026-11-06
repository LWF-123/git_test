package com.lwf.hospital.service.impl;

import com.lwf.hospital.common.exception.BizException;
import com.lwf.hospital.common.exception.ValidationException;
import com.lwf.hospital.common.log.AppLogger;
import com.lwf.hospital.dao.DepartmentDao;
import com.lwf.hospital.domain.entity.Department;
import com.lwf.hospital.service.DepartmentService;

import java.util.List;

/**
 * 科室管理服务实现：编码唯一、名称必填、有人则不许删。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentDao departmentDao;

    public DepartmentServiceImpl(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    @Override
    public List<Department> listAll() {
        return departmentDao.findAll();
    }

    @Override
    public long create(String deptCode, String name, String location) {
        String code = requireCode(deptCode);
        String deptName = requireName(name);
        departmentDao.findByCode(code).ifPresent(existing -> {
            throw new BizException("科室编码已存在：" + code + "（" + existing.getName() + "）");
        });
        long id = departmentDao.insert(code, deptName, trimToEmpty(location));
        AppLogger.info("新增科室：" + code + " " + deptName + "，id=" + id);
        return id;
    }

    @Override
    public void update(long id, String deptCode, String name, String location) {
        Department existing = departmentDao.findById(id)
                .orElseThrow(() -> new BizException("科室不存在（id=" + id + "）。"));
        String code = requireCode(deptCode);
        String deptName = requireName(name);
        departmentDao.findByCode(code)
                .filter(other -> !other.getId().equals(existing.getId()))
                .ifPresent(other -> {
                    throw new BizException("科室编码已被 " + other.getName() + " 占用：" + code);
                });
        departmentDao.update(id, code, deptName, trimToEmpty(location));
        AppLogger.info("修改科室：id=" + id + " -> " + code + " " + deptName);
    }

    @Override
    public void delete(long id) {
        departmentDao.findById(id)
                .orElseThrow(() -> new BizException("科室不存在（id=" + id + "）。"));
        int doctors = departmentDao.countDoctors(id);
        if (doctors > 0) {
            throw new BizException("该科室下还有 " + doctors + " 名医生，请先转移或停用后再删除。");
        }
        departmentDao.delete(id);
        AppLogger.info("删除科室：id=" + id);
    }

    private String requireCode(String deptCode) {
        if (deptCode == null || deptCode.isBlank()) {
            throw new ValidationException("科室编码不能为空。");
        }
        String code = deptCode.trim().toUpperCase();
        if (code.length() > 10) {
            throw new ValidationException("科室编码不能超过 10 个字符。");
        }
        return code;
    }

    private String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("科室名称不能为空。");
        }
        String value = name.trim();
        if (value.length() > 50) {
            throw new ValidationException("科室名称不能超过 50 个字符。");
        }
        return value;
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}