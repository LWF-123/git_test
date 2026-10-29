package com.lwf.hospital.common.util;

import com.lwf.hospital.common.exception.ValidationException;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * 控制台输入工具：统一处理空输入、非法数字与密码隐藏。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public final class InputUtils {

    private static final BufferedReader READER =
            new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));

    private InputUtils() {
    }

    /**
     * 读取一行；输入流结束时返回 "0"，等价于“返回/退出”。
     *
     * @param prompt 提示语
     * @return 用户输入
     */
    public static String readLine(String prompt) {
        System.out.print(prompt);
        try {
            String line = READER.readLine();
            return line == null ? "0" : line.trim();
        } catch (IOException e) {
            throw new ValidationException("读取输入失败：" + e.getMessage(), e);
        }
    }

    /**
     * 读取非空文本。
     *
     * @param prompt 提示语
     * @return 非空文本
     */
    public static String readNonBlank(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isBlank()) {
                return value;
            }
            System.out.println("  输入不能为空，请重新输入。");
        }
    }

    /**
     * 读取指定范围内的整数。
     *
     * @param prompt 提示语
     * @param min    最小值（含）
     * @param max    最大值（含）
     * @return 整数
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            String value = readLine(prompt);
            if (value.isBlank()) {
                System.out.println("  请输入数字。");
                continue;
            }
            try {
                int number = Integer.parseInt(value);
                if (number < min || number > max) {
                    System.out.println("  请输入 " + min + " ~ " + max + " 之间的数字。");
                    continue;
                }
                return number;
            } catch (NumberFormatException e) {
                System.out.println("  “" + value + "”不是合法数字。");
            }
        }
    }

    /**
     * 读取密码（控制台支持时隐藏输入）。
     *
     * @param prompt 提示语
     * @return 密码
     */
    public static String readPassword(String prompt) {
        Console console = System.console();
        if (console != null) {
            char[] chars = console.readPassword(prompt);
            return chars == null ? "" : new String(chars).trim();
        }
        return readLine(prompt);
    }

    /**
     * 读取确认（y/n）。
     *
     * @param prompt 提示语
     * @return 确认返回 true
     */
    public static boolean confirm(String prompt) {
        while (true) {
            String value = readLine(prompt + " (y/n)：");
            if ("y".equalsIgnoreCase(value)) {
                return true;
            }
            if ("n".equalsIgnoreCase(value)) {
                return false;
            }
            System.out.println("  请输入 y 或 n。");
        }
    }
}