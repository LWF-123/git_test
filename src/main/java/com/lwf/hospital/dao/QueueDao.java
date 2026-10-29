package com.lwf.hospital.dao;

import com.lwf.hospital.domain.entity.QueueTicket;
import com.lwf.hospital.domain.enums.TicketStatus;

import java.sql.Connection;
import java.util.Optional;

/**
 * 叫号队列数据访问接口。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public interface QueueDao {

    /**
     * 入队。
     *
     * @param connection     事务连接
     * @param registrationId 挂号单 id
     * @param scheduleId     排班 id
     * @param queueNo        排队号
     * @param priority       优先级
     * @return 队列 id
     */
    long insert(Connection connection, long registrationId, long scheduleId, int queueNo, int priority);

    /**
     * 事务内锁定下一位候诊者（按优先级、排队号排序取第一条）。
     *
     * @param connection 事务连接
     * @param scheduleId 排班 id
     * @return 队列票据
     */
    Optional<QueueTicket> lockFirstWaiting(Connection connection, long scheduleId);

    /**
     * 更新队列状态，{@code CALLING} 时同时写入叫号时间。
     *
     * @param connection     事务连接
     * @param registrationId 挂号单 id
     * @param status         新状态
     * @return 受影响行数
     */
    int updateStatus(Connection connection, long registrationId, TicketStatus status);
}