package com.lwf.hospital.common.util;

import java.util.List;

/**
 * 控制台表格渲染：按 Unicode 显示宽度对齐（中文按 2 列宽计算）。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public final class ConsoleTable {

    private ConsoleTable() {
    }

    /**
     * 输出表格。
     *
     * @param headers 表头
     * @param rows    数据行
     */
    public static void print(List<String> headers, List<List<String>> rows) {
        int columns = headers.size();
        int[] widths = new int[columns];
        for (int i = 0; i < columns; i++) {
            widths[i] = displayWidth(headers.get(i));
        }
        for (List<String> row : rows) {
            for (int i = 0; i < columns; i++) {
                widths[i] = Math.max(widths[i], displayWidth(cell(row, i)));
            }
        }

        String border = border(widths);
        System.out.println(border);
        System.out.println(rowLine(headers, widths));
        System.out.println(border);
        for (List<String> row : rows) {
            System.out.println(rowLine(row, widths));
        }
        System.out.println(border);
        if (rows.isEmpty()) {
            System.out.println("  （暂无数据）");
        }
    }

    private static String border(int[] widths) {
        StringBuilder builder = new StringBuilder("+");
        for (int width : widths) {
            builder.append("-".repeat(width + 2)).append('+');
        }
        return builder.toString();
    }

    private static String rowLine(List<String> row, int[] widths) {
        StringBuilder builder = new StringBuilder("|");
        for (int i = 0; i < widths.length; i++) {
            String text = cell(row, i);
            builder.append(' ').append(text)
                    .append(" ".repeat(Math.max(0, widths[i] - displayWidth(text)))).append(" |");
        }
        return builder.toString();
    }

    private static String cell(List<String> row, int index) {
        if (row == null || index >= row.size() || row.get(index) == null) {
            return "";
        }
        return row.get(index);
    }

    /**
     * 计算显示宽度（全角字符按 2 计）。
     *
     * @param text 文本
     * @return 宽度
     */
    public static int displayWidth(String text) {
        if (text == null) {
            return 0;
        }
        int width = 0;
        for (int codePoint : text.codePoints().toArray()) {
            width += isWide(codePoint) ? 2 : 1;
        }
        return width;
    }

    private static boolean isWide(int codePoint) {
        return (codePoint >= 0x1100 && codePoint <= 0x115F)
                || (codePoint >= 0x2E80 && codePoint <= 0xA4CF && codePoint != 0x303F)
                || (codePoint >= 0xAC00 && codePoint <= 0xD7A3)
                || (codePoint >= 0xF900 && codePoint <= 0xFAFF)
                || (codePoint >= 0xFE30 && codePoint <= 0xFE6F)
                || (codePoint >= 0xFF00 && codePoint <= 0xFF60)
                || (codePoint >= 0xFFE0 && codePoint <= 0xFFE6);
    }
}