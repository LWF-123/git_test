package com.lwf.hospital.common.exception;

import com.lwf.hospital.common.log.AppLogger;

/**
 * 全局异常兜底：把各类异常转换成用户看得懂的中文提示，并写入日志。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public final class GlobalExceptionHandler {

    private GlobalExceptionHandler() {
    }

    /**
     * 安装线程级未捕获异常处理器。
     */
    public static void install() {
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> handle(error));
    }

    /**
     * 统一处理入口。
     *
     * @param error 任意异常
     */
    public static void handle(Throwable error) {
        String message;
        if (error instanceof BizException || error instanceof ValidationException || error instanceof AuthException) {
            message = "提示：" + error.getMessage();
            AppLogger.warn(error.getClass().getSimpleName() + " - " + error.getMessage());
        } else if (error instanceof DataAccessException) {
            message = "数据库访问失败：" + error.getMessage()
                    + System.lineSeparator() + "请检查 MySQL 是否已启动、db.properties 是否正确、是否执行过 docs/sql 下的脚本。";
            AppLogger.error("数据库异常", error);
        } else if (error instanceof AppException) {
            message = "操作失败：" + error.getMessage();
            AppLogger.error("应用异常", error);
        } else {
            message = "系统内部错误：" + error.getMessage() + System.lineSeparator() + "详细信息已写入 logs/app-*.log。";
            AppLogger.error("未处理异常", error);
        }
        System.out.println();
        System.out.println("  ✗ " + message);
        System.out.println();
    }
}