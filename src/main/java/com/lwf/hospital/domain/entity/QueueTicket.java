package com.lwf.hospital.domain.entity;

import com.lwf.hospital.domain.enums.TicketStatus;

import java.time.LocalDateTime;

/**
 * 叫号队列票据：与挂号单一对一，单独成表便于“按优先级 + 排队号”取号。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class QueueTicket {

    private Long id;
    private Long registrationId;
    private Long scheduleId;
    private int queueNo;
    private int priority;
    private TicketStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime calledAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getQueueNo() {
        return queueNo;
    }

    public void setQueueNo(int queueNo) {
        this.queueNo = queueNo;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCalledAt() {
        return calledAt;
    }

    public void setCalledAt(LocalDateTime calledAt) {
        this.calledAt = calledAt;
    }

    @Override
    public String toString() {
        return "QueueTicket{" + queueNo + "号 priority=" + priority + " " + status + "}";
    }
}