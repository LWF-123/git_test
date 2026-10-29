package com.lwf.hospital.common.exception;

/**
 * 业务规则异常：请求合法但被规则拒绝，例如号源已满、重复挂号。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class BizException extends AppException {

    private static final long serialVersionUID = 1L;

    public BizException(String message) {
        super("BIZ", message);
    }

    public BizException(String message, Throwable cause) {
        super("BIZ", message, cause);
    }
}