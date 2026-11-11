package com.lwf.hospital.service.impl;

import com.lwf.hospital.common.exception.BizException;
import com.lwf.hospital.common.exception.ValidationException;
import com.lwf.hospital.common.log.AppLogger;
import com.lwf.hospital.dao.DoctorDao;
import com.lwf.hospital.dao.OperationLogDao;
import com.lwf.hospital.dao.RegistrationDao;
import com.lwf.hospital.dao.ScheduleDao;
import com.lwf.hospital.domain.dto.ScheduleView;
import com.lwf.hospital.domain.entity.Doctor;
import com.lwf.hospital.domain.entity.DoctorSchedule;
import com.lwf.hospital.service.ScheduleAdminService;

import java.time.LocalDate;
import java.util.List;

/**
 * 排班管理服务实现。
 *
 * <p>几条硬规则：日期不能早于今天；同医生同天同时段只能有一条；
 * 容量不能小于已挂号数（否则等于把已挂出去的人挤掉）；停诊只影响新挂号，
 * 已挂的患者需要人工告知或退号。</p>
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class ScheduleAdminServiceImpl implements ScheduleAdminService {

    private static final int MAX_QUOTA = 200;
    private static final int MAX_ADD = 50;

    private final ScheduleDao scheduleDao;
    private final DoctorDao doctorDao;
    private final RegistrationDao registrationDao;
    private final OperationLogDao operationLogDao;

    public ScheduleAdminServiceImpl(ScheduleDao scheduleDao, DoctorDao doctorDao,
                                    RegistrationDao registrationDao, OperationLogDao operationLogDao) {
        this.scheduleDao = scheduleDao;
        this.doctorDao = doctorDao;
        this.registrationDao = registrationDao;
        this.operationLogDao = operationLogDao;
    }

    @Override
    public List<ScheduleView> list(LocalDate from, LocalDate to, long departmentId) {
        if (from == null || to == null) {
            throw new ValidationException("日期区间不能为空。");
        }
        if (to.isBefore(from)) {
            throw new ValidationException("结束日期不能早于起始日期。");
        }
        return scheduleDao.findByDateRange(from, to, departmentId);
    }

    @Override
    public long create(long doctorId, LocalDate workDate, String period, int quotaTotal) {
        if (workDate == null) {
            throw new ValidationException("出诊日期不能为空。");
        }
        if (workDate.isBefore(LocalDate.now())) {
            throw new ValidationException("排班日期不能早于今天（" + LocalDate.now() + "）。");
        }
        String slot = requirePeriod(period);
        requireQuota(quotaTotal);
        Doctor doctor = doctorDao.findById(doctorId)
                .orElseThrow(() -> new ValidationException("医生不存在（id=" + doctorId + "）。"));
        if (!doctor.isEnabled()) {
            throw new BizException("医生 " + doctor.getName() + " 已停用，请先恢复后再排班。");
        }
        scheduleDao.findByDoctorDatePeriod(doctorId, workDate, slot).ifPresent(existing -> {
            throw new BizException("该医生 " + workDate + " " + periodText(slot)
                    + " 已有排班（id=" + existing.getId() + "），请直接修改容量。");
        });
        long id = scheduleDao.insert(doctorId, workDate, slot, quotaTotal);
        AppLogger.info("新增排班：医生 " + doctorId + "，" + workDate + " " + slot + "，容量 " + quotaTotal);
        return id;
    }

    @Override
    public void updateQuota(long scheduleId, int quotaTotal) {
        DoctorSchedule schedule = require(scheduleId);
        if (quotaTotal < 1 || quotaTotal > MAX_QUOTA) {
            throw new ValidationException("号源总量应在 1 ~ " + MAX_QUOTA + " 之间。");
        }
        if (quotaTotal < schedule.getQuotaUsed()) {
            throw new BizException("新容量 " + quotaTotal + " 不能小于已挂号数 "
                    + schedule.getQuotaUsed() + "。");
        }
        int rows = scheduleDao.updateQuotaTotal(scheduleId, quotaTotal);
        if (rows == 0) {
            throw new BizException("修改失败：期间有新的挂号，请刷新后重试。");
        }
        AppLogger.info("修改号源：排班 " + scheduleId + " 容量 " + schedule.getQuotaTotal()
                + " -> " + quotaTotal);
    }

    @Override
    public int close(long scheduleId) {
        DoctorSchedule schedule = require(scheduleId);
        if (!"OPEN".equalsIgnoreCase(schedule.getStatus())) {
            throw new BizException("该排班已经是停诊状态。");
        }
        if (schedule.getWorkDate().isBefore(LocalDate.now())) {
            throw new BizException("该排班日期已过（" + schedule.getWorkDate() + "），无需停诊。");
        }
        scheduleDao.updateStatus(scheduleId, "CLOSED");
        int waiting = registrationDao.countWaitingBySchedule(scheduleId);
        AppLogger.info("停诊：排班 " + scheduleId + "，候诊人数 " + waiting);
        return waiting;
    }

    @Override
    public void open(long scheduleId) {
        DoctorSchedule schedule = require(scheduleId);
        if ("OPEN".equalsIgnoreCase(schedule.getStatus())) {
            throw new BizException("该排班已经是开诊状态。");
        }
        if (schedule.getWorkDate().isBefore(LocalDate.now())) {
            throw new BizException("该排班日期已过（" + schedule.getWorkDate() + "），不能恢复开诊。");
        }
        scheduleDao.updateStatus(scheduleId, "OPEN");
        AppLogger.info("恢复开诊：排班 " + scheduleId);
    }

    @Override
    public void addQuota(long scheduleId, int extra, long operatorId) {
        DoctorSchedule schedule = require(scheduleId);
        if (extra < 1 || extra > MAX_ADD) {
            throw new ValidationException("单次加号数量应在 1 ~ " + MAX_ADD + " 之间。");
        }
        int newTotal = schedule.getQuotaTotal() + extra;
        if (newTotal > MAX_QUOTA) {
            throw new BizException("加号后总量 " + newTotal + " 超过上限 " + MAX_QUOTA + "。");
        }
        scheduleDao.addQuotaTotal(scheduleId, extra);
        operationLogDao.insert(operatorId, "ADD_QUOTA",
                "排班 " + scheduleId + " 加号 " + extra + "，总量 "
                        + schedule.getQuotaTotal() + " -> " + newTotal);
        AppLogger.info("加号：排班 " + scheduleId + " +" + extra + "，总量 " + newTotal);
    }

    private DoctorSchedule require(long scheduleId) {
        return scheduleDao.findById(scheduleId)
                .orElseThrow(() -> new BizException("排班不存在（id=" + scheduleId + "）。"));
    }

    private String requirePeriod(String period) {
        if (period == null) {
            throw new ValidationException("时段只能是 AM 或 PM。");
        }
        String value = period.trim().toUpperCase();
        if ("上午".equals(period.trim())) {
            value = "AM";
        } else if ("下午".equals(period.trim())) {
            value = "PM";
        }
        if (!"AM".equals(value) && !"PM".equals(value)) {
            throw new ValidationException("时段只能是 AM（上午）或 PM（下午）。");
        }
        return value;
    }

    private void requireQuota(int quotaTotal) {
        if (quotaTotal < 1 || quotaTotal > MAX_QUOTA) {
            throw new ValidationException("号源总量应在 1 ~ " + MAX_QUOTA + " 之间。");
        }
    }

    private String periodText(String period) {
        return "AM".equals(period) ? "上午" : "下午";
    }
}