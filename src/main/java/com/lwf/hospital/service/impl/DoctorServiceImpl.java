package com.lwf.hospital.service.impl;

import com.lwf.hospital.common.exception.BizException;
import com.lwf.hospital.common.exception.ValidationException;
import com.lwf.hospital.common.log.AppLogger;
import com.lwf.hospital.dao.DepartmentDao;
import com.lwf.hospital.dao.DoctorDao;
import com.lwf.hospital.dao.JdbcHelper;
import com.lwf.hospital.dao.ScheduleDao;
import com.lwf.hospital.domain.dto.DoctorView;
import com.lwf.hospital.domain.entity.Doctor;
import com.lwf.hospital.service.DoctorService;

import java.util.List;
import java.util.Objects;

/**
 * 医生管理服务实现。
 *
 * <p>停用一个医生不只是改个状态：还要把他今天及以后的号源一起关闭，
 * 否则已经挂出去、或者即将被挂出去的号会变成“挂给一个停诊医生”。</p>
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class DoctorServiceImpl implements DoctorService {

    private final JdbcHelper jdbc;
    private final DoctorDao doctorDao;
    private final DepartmentDao departmentDao;
    private final ScheduleDao scheduleDao;

    public DoctorServiceImpl(JdbcHelper jdbc, DoctorDao doctorDao,
                             DepartmentDao departmentDao, ScheduleDao scheduleDao) {
        this.jdbc = jdbc;
        this.doctorDao = doctorDao;
        this.departmentDao = departmentDao;
        this.scheduleDao = scheduleDao;
    }

    @Override
    public List<DoctorView> listAll() {
        return doctorDao.findAll();
    }

    @Override
    public List<DoctorView> listByDepartment(long departmentId) {
        return doctorDao.findByDepartment(departmentId);
    }

    @Override
    public long create(String doctorNo, String name, String title, long departmentId) {
        String no = requireNo(doctorNo);
        String doctorName = requireName(name);
        String doctorTitle = requireTitle(title);
        requireDepartment(departmentId);
        doctorDao.findByNo(no).ifPresent(existing -> {
            throw new BizException("工号已存在：" + no + "（" + existing.getName() + "）");
        });
        long id = doctorDao.insert(no, doctorName, doctorTitle, departmentId);
        AppLogger.info("新增医生：" + no + " " + doctorName + "，id=" + id);
        return id;
    }

    @Override
    public void update(long id, String doctorNo, String name, String title, long departmentId) {
        Doctor existing = doctorDao.findById(id)
                .orElseThrow(() -> new BizException("医生不存在（id=" + id + "）。"));
        String no = requireNo(doctorNo);
        String doctorName = requireName(name);
        String doctorTitle = requireTitle(title);
        requireDepartment(departmentId);
        doctorDao.findByNo(no)
                .filter(other -> !Objects.equals(other.getId(), existing.getId()))
                .ifPresent(other -> {
                    throw new BizException("工号已被 " + other.getName() + " 占用：" + no);
                });
        doctorDao.update(id, no, doctorName, doctorTitle, departmentId);
        AppLogger.info("修改医生：id=" + id + " -> " + no + " " + doctorName);
    }

    @Override
    public int disable(long id) {
        Doctor existing = doctorDao.findById(id)
                .orElseThrow(() -> new BizException("医生不存在（id=" + id + "）。"));
        if (!existing.isEnabled()) {
            throw new BizException("医生 " + existing.getName() + " 已经是停用状态。");
        }
        int closed = jdbc.inTransaction(connection -> {
            doctorDao.setEnabled(id, false);
            return scheduleDao.closeFutureSchedules(connection, id);
        });
        AppLogger.info("停用医生：id=" + id + "，同时关闭排班 " + closed + " 条");
        return closed;
    }

    @Override
    public void enable(long id) {
        Doctor existing = doctorDao.findById(id)
                .orElseThrow(() -> new BizException("医生不存在（id=" + id + "）。"));
        if (existing.isEnabled()) {
            throw new BizException("医生 " + existing.getName() + " 已经是启用状态。");
        }
        doctorDao.setEnabled(id, true);
        AppLogger.info("恢复医生：id=" + id);
    }

    private void requireDepartment(long departmentId) {
        departmentDao.findById(departmentId)
                .orElseThrow(() -> new ValidationException("科室不存在（id=" + departmentId + "）。"));
    }

    private String requireNo(String doctorNo) {
        if (doctorNo == null || doctorNo.isBlank()) {
            throw new ValidationException("工号不能为空。");
        }
        String no = doctorNo.trim();
        if (no.length() > 20) {
            throw new ValidationException("工号不能超过 20 个字符。");
        }
        return no;
    }

    private String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("医生姓名不能为空。");
        }
        String value = name.trim();
        if (value.length() > 50) {
            throw new ValidationException("医生姓名不能超过 50 个字符。");
        }
        return value;
    }

    private String requireTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new ValidationException("职称不能为空（如 主任医师/副主任医师/主治医师/医师）。");
        }
        String value = title.trim();
        if (value.length() > 30) {
            throw new ValidationException("职称不能超过 30 个字符。");
        }
        return value;
    }
}