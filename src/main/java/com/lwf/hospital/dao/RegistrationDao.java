package com.lwf.hospital.dao;

import com.lwf.hospital.domain.dto.RegistrationView;
import com.lwf.hospital.domain.entity.Registration;
import com.lwf.hospital.domain.enums.RegistrationType;
import com.lwf.hospital.domain.enums.TicketStatus;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * 挂号单数据访问接口。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public interface RegistrationDao {

    /**
     * 某患者在某排班下仍有效的挂号记录（用于防重复挂号）。
     *
     * @param connection 事务连接
     * @param patientId  患者 id
     * @param scheduleId 排班 id
     * @return 挂号记录
     */
    Optional<Registration> findActive(Connection connection, long patientId, long scheduleId);

    /**
     * 生成下一个排队号（同一排班内递增）。
     *
     * @param connection 事务连接
     * @param scheduleId 排班 id
     * @return 排队号
     */
    int nextQueueNo(Connection connection, long scheduleId);

    /**
     * 插入挂号单。
     *
     * @param connection 事务连接
     * @param patientId  患者 id
     * @param scheduleId 排班 id
     * @param type       挂号类型
     * @param queueNo    排队号
     * @return 挂号单 id
     */
    long insert(Connection connection, long patientId, long scheduleId, RegistrationType type, int queueNo);

    /**
     * 更新挂号单状态。
     *
     * @param connection     事务连接
     * @param registrationId 挂号单 id
     * @param status         新状态
     * @return 受影响行数
     */
    int updateStatus(Connection connection, long registrationId, TicketStatus status);

    /**
     * 某患者的挂号记录（含状态与排队号）。
     *
     * @param patientId 患者 id
     * @return 挂号记录视图
     */
    List<RegistrationView> findByPatient(long patientId);

    /**
     * 某排班的候诊队列（WAITING / CALLING），按优先级 + 排队号排序。
     *
     * @param scheduleId 排班 id
     * @return 队列视图
     */
    List<RegistrationView> findQueue(long scheduleId);

    /**
     * 按 id 查询挂号视图（挂号成功后回显用）。
     *
     * @param registrationId 挂号单 id
     * @return 挂号视图
     */
    Optional<RegistrationView> findView(long registrationId);
}