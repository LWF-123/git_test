package com.lwf.hospital.service.impl;

import com.lwf.hospital.common.exception.BizException;
import com.lwf.hospital.common.exception.ValidationException;
import com.lwf.hospital.common.log.AppLogger;
import com.lwf.hospital.dao.JdbcHelper;
import com.lwf.hospital.dao.QueueDao;
import com.lwf.hospital.dao.RegistrationDao;
import com.lwf.hospital.dao.ScheduleDao;
import com.lwf.hospital.domain.dto.RegistrationView;
import com.lwf.hospital.domain.dto.ScheduleView;
import com.lwf.hospital.domain.entity.Department;
import com.lwf.hospital.domain.entity.Doctor;
import com.lwf.hospital.domain.entity.DoctorSchedule;
import com.lwf.hospital.domain.entity.Registration;
import com.lwf.hospital.domain.enums.RegistrationType;
import com.lwf.hospital.service.RegistrationService;
import com.lwf.hospital.service.validation.RegistrationContext;
import com.lwf.hospital.service.validation.RegistrationRuleChain;
import com.lwf.hospital.service.validation.rules.DuplicateRule;
import com.lwf.hospital.service.validation.rules.QuotaRule;

import java.util.List;

/**
 * 挂号服务实现：并发控制与事务边界的核心示例。
 *
 * <p>一次挂号 = 锁排班 → 规则链 → 条件更新占号 → 生成排队号 → 写挂号单与队列，全部在同一事务内完成。</p>
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class RegistrationServiceImpl implements RegistrationService {

    private final JdbcHelper jdbc;
    private final ScheduleDao scheduleDao;
    private final RegistrationDao registrationDao;
    private final QueueDao queueDao;
    private final RegistrationRuleChain ruleChain;

    public RegistrationServiceImpl(JdbcHelper jdbc,
                                   ScheduleDao scheduleDao,
                                   RegistrationDao registrationDao,
                                   QueueDao queueDao) {
        this.jdbc = jdbc;
        this.scheduleDao = scheduleDao;
        this.registrationDao = registrationDao;
        this.queueDao = queueDao;
        this.ruleChain = new RegistrationRuleChain()
                .add(new QuotaRule())
                .add(new DuplicateRule());
    }

    @Override
    public List<Department> listDepartments() {
        return scheduleDao.findDepartments();
    }

    @Override
    public List<Doctor> listDoctors(long departmentId) {
        return scheduleDao.findDoctorsByDepartment(departmentId);
    }

    @Override
    public List<ScheduleView> listAvailableSchedules(long departmentId) {
        return scheduleDao.findAvailable(departmentId);
    }

    @Override
    public RegistrationView register(long patientId, long scheduleId, RegistrationType type) {
        if (type == null) {
            throw new ValidationException("请选择挂号类型。");
        }
        long registrationId = jdbc.inTransaction(connection -> {
            DoctorSchedule schedule = scheduleDao.lockForUpdate(connection, scheduleId)
                    .orElseThrow(() -> new BizException("排班不存在（id=" + scheduleId + "）。"));
            if (!schedule.isOpen()) {
                throw new BizException("该时段当前不可挂号（" + ("OPEN".equalsIgnoreCase(schedule.getStatus()) ? "号源已满" : "已停诊") + "）。");
            }

            Registration existing = registrationDao.findActive(connection, patientId, scheduleId).orElse(null);
            ruleChain.check(new RegistrationContext(patientId, schedule, existing, type));

            if (scheduleDao.tryIncreaseQuota(connection, scheduleId) == 0) {
                throw new BizException("号源已满，挂号失败（可能刚被其他患者挂完）。");
            }
            int queueNo = registrationDao.nextQueueNo(connection, scheduleId);
            long id = registrationDao.insert(connection, patientId, scheduleId, type, queueNo);
            queueDao.insert(connection, id, scheduleId, queueNo, type.getPriority());
            return id;
        });

        AppLogger.info("挂号成功：patient=" + patientId + ", schedule=" + scheduleId + ", registration=" + registrationId);
        return registrationDao.findView(registrationId)
                .orElseThrow(() -> new BizException("挂号已生成，但读取挂号单失败，请稍后在“我的挂号”中查看。"));
    }

    @Override
    public List<RegistrationView> myRegistrations(long patientId) {
        return registrationDao.findByPatient(patientId);
    }

    /**
     * 规则链中的规则名称（启动时打印，便于答辩讲解）。
     *
     * @return 规则名称列表
     */
    public List<String> ruleNames() {
        return ruleChain.ruleNames();
    }
}