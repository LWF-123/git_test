package com.lwf.hospital.service.impl;

import com.lwf.hospital.common.exception.BizException;
import com.lwf.hospital.common.log.AppLogger;
import com.lwf.hospital.dao.JdbcHelper;
import com.lwf.hospital.dao.QueueDao;
import com.lwf.hospital.dao.RegistrationDao;
import com.lwf.hospital.domain.dto.RegistrationView;
import com.lwf.hospital.domain.entity.QueueTicket;
import com.lwf.hospital.domain.enums.TicketStatus;
import com.lwf.hospital.service.QueueService;

import java.util.List;

/**
 * 叫号服务实现。
 *
 * <p>“叫下一位”在一个事务里完成：锁定队首（{@code FOR UPDATE}）→ 队列置为 CALLING →
 * 挂号单置为 CALLING。两个人同时点“下一位”也只会叫到不同的人。</p>
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class QueueServiceImpl implements QueueService {

    private final JdbcHelper jdbc;
    private final RegistrationDao registrationDao;
    private final QueueDao queueDao;

    public QueueServiceImpl(JdbcHelper jdbc, RegistrationDao registrationDao, QueueDao queueDao) {
        this.jdbc = jdbc;
        this.registrationDao = registrationDao;
        this.queueDao = queueDao;
    }

    @Override
    public List<RegistrationView> waitingQueue(long scheduleId) {
        return registrationDao.findQueue(scheduleId);
    }

    @Override
    public RegistrationView callNext(long scheduleId) {
        Long registrationId = jdbc.inTransaction(connection -> {
            QueueTicket ticket = queueDao.lockFirstWaiting(connection, scheduleId).orElse(null);
            if (ticket == null) {
                return null;
            }
            queueDao.updateStatus(connection, ticket.getRegistrationId(), TicketStatus.CALLING);
            registrationDao.updateStatus(connection, ticket.getRegistrationId(), TicketStatus.CALLING);
            return ticket.getRegistrationId();
        });

        if (registrationId == null) {
            return null;
        }
        AppLogger.info("叫号：schedule=" + scheduleId + ", registration=" + registrationId);
        return registrationDao.findView(registrationId).orElse(null);
    }

    @Override
    public void skip(long registrationId) {
        changeStatus(registrationId, TicketStatus.SKIPPED, "过号");
    }

    @Override
    public void complete(long registrationId) {
        changeStatus(registrationId, TicketStatus.SERVED, "完成接诊");
    }

    private void changeStatus(long registrationId, TicketStatus target, String action) {
        jdbc.inTransaction(connection -> {
            int affected = queueDao.updateStatus(connection, registrationId, target);
            if (affected == 0) {
                throw new BizException("该挂号单不在候诊队列中，无法" + action + "。");
            }
            registrationDao.updateStatus(connection, registrationId, target);
            return null;
        });
        AppLogger.info(action + "：registration=" + registrationId);
    }
}