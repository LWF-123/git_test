package com.lwf.hospital.service;

import com.lwf.hospital.domain.dto.RegistrationView;

import java.util.List;

/**
 * 叫号服务：候诊队列、叫下一位、过号、完成接诊。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public interface QueueService {

    /**
     * 某排班的候诊队列（按优先级与排队号排序）。
     *
     * @param scheduleId 排班 id
     * @return 队列视图
     */
    List<RegistrationView> waitingQueue(long scheduleId);

    /**
     * 叫下一位。
     *
     * @param scheduleId 排班 id
     * @return 被叫到的患者；队列为空时返回 null
     */
    RegistrationView callNext(long scheduleId);

    /**
     * 过号。
     *
     * @param registrationId 挂号单 id
     */
    void skip(long registrationId);

    /**
     * 完成接诊。
     *
     * @param registrationId 挂号单 id
     */
    void complete(long registrationId);
}