package com.lwf.hospital.common.exception;

/**
 * 数据访问异常：统一包装 {@link java.sql.SQLException}，让上层不依赖 java.sql。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class DataAccessException extends AppException {

    private static final long serialVersionUID = 1L;

    public DataAccessException(String message) {
        super("DB", message);
    }

    public DataAccessException(String message, Throwable cause) {
        super("DB", message, cause);
    }
}