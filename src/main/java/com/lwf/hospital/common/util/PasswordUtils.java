package com.lwf.hospital.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * 口令工具：加盐 SHA-256 摘要 + 随机盐。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public final class PasswordUtils {

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtils() {
    }

    /**
     * 生成 8 字节随机盐（16 位十六进制）。
     *
     * @return 盐值
     */
    public static String randomSalt() {
        byte[] bytes = new byte[8];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    /**
     * 计算摘要：{@code SHA-256(salt + ":" + rawPassword)}。
     *
     * @param rawPassword 明文口令
     * @param salt        盐
     * @return 64 位小写十六进制摘要
     */
    public static String hash(String rawPassword, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update((salt + ":" + rawPassword).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("当前 JVM 不支持 SHA-256", e);
        }
    }

    /**
     * 校验口令。
     *
     * @param rawPassword  明文口令
     * @param salt         盐
     * @param expectedHash 数据库中的摘要
     * @return 匹配返回 true
     */
    public static boolean matches(String rawPassword, String salt, String expectedHash) {
        return expectedHash != null && expectedHash.equalsIgnoreCase(hash(rawPassword, salt));
    }
}