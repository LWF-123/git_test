package com.lwf.hospital.common.exception;

/**
 * 系统统一异常基类，便于界面层统一捕获与提示。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class AppException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String code;

    public AppException(String message) {
        this("APP_ERROR", message, null);
    }

    public AppException(String message, Throwable cause) {
        this("APP_ERROR", message, cause);
    }

    public AppException(String code, String message) {
        this(code, message, null);
    }

    public AppException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * 错误码。
     *
     * @return 错误码
     */
    public String getCode() {
        return code;
    }
}