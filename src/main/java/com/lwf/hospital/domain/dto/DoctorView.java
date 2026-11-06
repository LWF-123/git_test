package com.lwf.hospital.domain.dto;

/**
 * 医生列表视图（带科室名与在职状态），供管理员端展示。
 *
 * @param id         医生 id
 * @param doctorNo   工号
 * @param name       姓名
 * @param title      职称
 * @param deptName   科室名称
 * @param enabled    是否在职
 * @author LWF-123
 * @since 1.0.0
 */
public record DoctorView(
        long id,
        String doctorNo,
        String name,
        String title,
        String deptName,
        boolean enabled) {

    /**
     * 状态文字。
     *
     * @return 在职 / 停用
     */
    public String statusText() {
        return enabled ? "在职" : "停用";
    }
}