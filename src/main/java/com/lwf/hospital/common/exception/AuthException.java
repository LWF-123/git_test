package com.lwf.hospital.common.exception;

/**
 * 认证异常：用户名/密码错误、账号被禁用。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class AuthException extends AppException {

    private static final long serialVersionUID = 1L;

    public AuthException(String message) {
        super("AUTH", message);
    }

    public AuthException(String message, Throwable cause) {
        super("AUTH", message, cause);
    }
}