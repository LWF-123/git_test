package com.lwf.hospital.domain.enums;

/**
 * 用户角色。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public enum RoleType {

    /** 管理员：科室、医生、排班维护与统计。 */
    ADMIN("ADMIN", "管理员"),

    /** 医生：查看候诊队列、叫号、过号、完成接诊。 */
    DOCTOR("DOCTOR", "医生"),

    /** 患者：挂号、查看挂号记录。 */
    PATIENT("PATIENT", "患者");

    private final String code;
    private final String label;

    RoleType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    /**
     * 由编码解析角色。
     *
     * @param code 编码
     * @return 角色
     */
    public static RoleType fromCode(String code) {
        for (RoleType role : values()) {
            if (role.code.equalsIgnoreCase(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("非法的角色编码：" + code);
    }
}