package com.lwf.hospital.domain.enums;

/**
 * 叫号状态：挂号单与队列票据共用一套状态，避免两处定义不一致。
 *
 * <pre>
 * WAITING ──叫号──&gt; CALLING ──完成──&gt; SERVED
 *    │                  │
 *    └──过号──&gt; SKIPPED &lt;┘
 * </pre>
 *
 * @author LWF-123
 * @since 1.0.0
 */
public enum TicketStatus {

    /** 候诊中。 */
    WAITING("WAITING", "候诊"),

    /** 正在叫号/就诊。 */
    CALLING("CALLING", "叫号中"),

    /** 已完成接诊。 */
    SERVED("SERVED", "已完成"),

    /** 已过号。 */
    SKIPPED("SKIPPED", "已过号"),

    /** 已取消（退号）。 */
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String label;

    TicketStatus(String code, String label) {
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
     * 是否还在队列里（可被叫到）。
     *
     * @return 候诊或叫号中返回 true
     */
    public boolean inQueue() {
        return this == WAITING || this == CALLING;
    }

    /**
     * 由编码解析状态。
     *
     * @param code 编码
     * @return 状态
     */
    public static TicketStatus fromCode(String code) {
        for (TicketStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("非法的叫号状态：" + code);
    }
}