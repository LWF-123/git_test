package com.lwf.hospital.domain.entity;

import com.lwf.hospital.domain.enums.RegistrationType;
import com.lwf.hospital.domain.enums.TicketStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 挂号单。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class Registration {

    private Long id;
    private Long patientId;
    private Long scheduleId;
    private RegistrationType type;
    private int queueNo;
    private int priority;
    private BigDecimal fee;
    private TicketStatus status;
    private LocalDateTime createdAt;

    /**
     * 是否仍在候诊队列中。
     *
     * @return 在队列中返回 true
     */
    public boolean inQueue() {
        return status != null && status.inQueue();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public RegistrationType getType() {
        return type;
    }

    public void setType(RegistrationType type) {
        this.type = type;
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

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
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

    @Override
    public String toString() {
        return "Registration{" + queueNo + "号 " + type + " " + status + "}";
    }
}