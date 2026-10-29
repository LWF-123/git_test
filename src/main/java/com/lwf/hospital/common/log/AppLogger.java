package com.lwf.hospital.common.log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * 轻量日志门面：基于 JDK 自带 java.util.logging，不引入第三方依赖。
 *
 * <p>控制台只做交互，日志统一落到 {@code logs/app-*.log}，避免日志冲掉菜单。</p>
 *
 * @author LWF-123
 * @since 1.0.0
 */
public final class AppLogger {

    private static final Logger LOGGER = Logger.getLogger("hospital-queue");
    private static boolean installed;

    private AppLogger() {
    }

    /**
     * 初始化日志文件（按 1MB 滚动，最多保留 3 个）。
     */
    public static synchronized void install() {
        if (installed) {
            return;
        }
        try {
            Files.createDirectories(Path.of("logs"));
            FileHandler handler = new FileHandler("logs/app-%g.log", 1_000_000, 3, true);
            handler.setEncoding("UTF-8");
            handler.setFormatter(new SimpleFormatter());
            handler.setLevel(Level.INFO);
            LOGGER.addHandler(handler);
            LOGGER.setUseParentHandlers(false);
            LOGGER.setLevel(Level.INFO);
            installed = true;
        } catch (IOException e) {
            System.err.println("初始化日志失败：" + e.getMessage());
        }
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void warn(String message) {
        LOGGER.warning(message);
    }

    public static void error(String message, Throwable error) {
        LOGGER.log(Level.SEVERE, message, error);
    }
}