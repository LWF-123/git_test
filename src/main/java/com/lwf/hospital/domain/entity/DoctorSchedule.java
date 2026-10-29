package com.lwf.hospital.domain.entity;

import java.time.LocalDate;

/**
 * 排班（号源）：某医生某天上午或下午的放号情况。
 *
 * <p>{@code quotaUsed} 的更新必须在事务内、用“带条件的 UPDATE”完成，避免并发超号。</p>
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class DoctorSchedule {

    private Long id;
    private Long doctorId;
    private LocalDate workDate;
    private String period;
    private int quotaTotal;
    private int quotaUsed;
    private String status;

    /**
     * 剩余号源。
     *
     * @return 剩余数量
     */
    public int remaining() {
        return Math.max(0, quotaTotal - quotaUsed);
    }

    /**
     * 是否可挂号。
     *
     * @return 开放且有剩余号源返回 true
     */
    public boolean isOpen() {
        return "OPEN".equalsIgnoreCase(status) && remaining() > 0;
    }

    /**
     * 中文时段。
     *
     * @return 上午/下午
     */
    public String periodText() {
        return "AM".equalsIgnoreCase(period) ? "上午" : "下午";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getQuotaTotal() {
        return quotaTotal;
    }

    public void setQuotaTotal(int quotaTotal) {
        this.quotaTotal = quotaTotal;
    }

    public int getQuotaUsed() {
        return quotaUsed;
    }

    public void setQuotaUsed(int quotaUsed) {
        this.quotaUsed = quotaUsed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Schedule{id=" + id + ", " + workDate + " " + period + ", " + quotaUsed + "/" + quotaTotal + "}";
    }
}