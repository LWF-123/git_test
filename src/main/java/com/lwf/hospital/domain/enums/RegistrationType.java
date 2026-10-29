package com.lwf.hospital.domain.enums;

import java.math.BigDecimal;

/**
 * 挂号类型：把“费用”和“叫号优先级”这两项随类型变化的行为放进枚举，避免到处写 if。
 *
 * <p>优先级数值越小越先被叫到：急诊（10）&lt; 专家（15）&lt; 普通（20）。</p>
 *
 * @author LWF-123
 * @since 1.0.0
 */
public enum RegistrationType {

    /** 普通号。 */
    NORMAL("NORMAL", "普通号", new BigDecimal("10.00"), 20),

    /** 专家号。 */
    EXPERT("EXPERT", "专家号", new BigDecimal("30.00"), 15),

    /** 急诊号，优先叫号。 */
    EMERGENCY("EMERGENCY", "急诊号", new BigDecimal("50.00"), 10);

    private final String code;
    private final String label;
    private final BigDecimal fee;
    private final int priority;

    RegistrationType(String code, String label, BigDecimal fee, int priority) {
        this.code = code;
        this.label = label;
        this.fee = fee;
        this.priority = priority;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    /**
     * 挂号费。
     *
     * @return 费用
     */
    public BigDecimal getFee() {
        return fee;
    }

    /**
     * 叫号优先级（越小越先）。
     *
     * @return 优先级
     */
    public int getPriority() {
        return priority;
    }

    /**
     * 由编码解析类型。
     *
     * @param code 编码
     * @return 类型
     */
    public static RegistrationType fromCode(String code) {
        for (RegistrationType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("非法的挂号类型：" + code);
    }
}