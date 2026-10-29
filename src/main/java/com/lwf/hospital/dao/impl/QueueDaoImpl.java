package com.lwf.hospital.dao.impl;

import com.lwf.hospital.dao.JdbcHelper;
import com.lwf.hospital.dao.QueueDao;
import com.lwf.hospital.domain.entity.QueueTicket;
import com.lwf.hospital.domain.enums.TicketStatus;

import java.sql.Connection;
import java.util.Optional;

/**
 * 叫号队列 DAO 的 JDBC 实现。
 *
 * <p>{@link #lockFirstWaiting(Connection, long)} 使用 {@code FOR UPDATE} 锁住队首，
 * 保证两个叫号窗口同时点“下一位”时不会叫到同一个人。</p>
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class QueueDaoImpl implements QueueDao {

    private final JdbcHelper jdbc;

    public QueueDaoImpl(JdbcHelper jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public long insert(Connection connection, long registrationId, long scheduleId, int queueNo, int priority) {
        return jdbc.insert(connection, """
                        INSERT INTO queue_ticket (registration_id, schedule_id, queue_no, priority, status)
                        VALUES (?, ?, ?, ?, 'WAITING')
                        """,
                registrationId, scheduleId, queueNo, priority);
    }

    @Override
    public Optional<QueueTicket> lockFirstWaiting(Connection connection, long scheduleId) {
        return jdbc.queryOne(connection, """
                        SELECT id, registration_id, schedule_id, queue_no, priority, status
                        FROM queue_ticket
                        WHERE schedule_id = ? AND status = 'WAITING'
                        ORDER BY priority, queue_no
                        LIMIT 1
                        FOR UPDATE
                        """,
                rs -> {
                    QueueTicket ticket = new QueueTicket();
                    ticket.setId(rs.getLong("id"));
                    ticket.setRegistrationId(rs.getLong("registration_id"));
                    ticket.setScheduleId(rs.getLong("schedule_id"));
                    ticket.setQueueNo(rs.getInt("queue_no"));
                    ticket.setPriority(rs.getInt("priority"));
                    ticket.setStatus(TicketStatus.fromCode(rs.getString("status")));
                    return ticket;
                }, scheduleId);
    }

    @Override
    public int updateStatus(Connection connection, long registrationId, TicketStatus status) {
        if (status == TicketStatus.CALLING) {
            return jdbc.update(connection,
                    "UPDATE queue_ticket SET status = ?, called_at = NOW() WHERE registration_id = ?",
                    status.getCode(), registrationId);
        }
        return jdbc.update(connection, "UPDATE queue_ticket SET status = ? WHERE registration_id = ?",
                status.getCode(), registrationId);
    }
}