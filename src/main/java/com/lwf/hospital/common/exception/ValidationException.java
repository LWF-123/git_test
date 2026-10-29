package com.lwf.hospital.common.exception;

/**
 * 参数校验异常：输入不合法，可提示后重试。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class ValidationException extends AppException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super("VALIDATION", message);
    }

    public ValidationException(String message, Throwable cause) {
        super("VALIDATION", message, cause);
    }
}