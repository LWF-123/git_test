package com.lwf.hospital;

import com.lwf.hospital.common.exception.GlobalExceptionHandler;
import com.lwf.hospital.common.log.AppLogger;
import com.lwf.hospital.context.AppContext;
import com.lwf.hospital.ui.ConsoleApp;

/**
 * 程序入口：只做全局异常兜底、日志初始化、上下文装配与界面启动。
 *
 * <p>业务逻辑一律放在 service 层，避免出现“所有代码堆在 Main 里”的问题。</p>
 *
 * @author LWF-123
 * @since 1.0.0
 */
public final class Main {

    private Main() {
    }

    /**
     * 启动入口。
     *
     * @param args 命令行参数（当前未使用）
     */
    public static void main(String[] args) {
        GlobalExceptionHandler.install();
        AppLogger.install();
        AppLogger.info("===== 门诊挂号与排队叫号系统启动 =====");

        try {
            new ConsoleApp(AppContext.bootstrap()).start();
        } catch (Exception e) {
            GlobalExceptionHandler.handle(e);
        } finally {
            AppLogger.info("===== 系统退出 =====");
        }
    }
}