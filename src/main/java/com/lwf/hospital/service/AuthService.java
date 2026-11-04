package com.lwf.hospital.service;

import com.lwf.hospital.domain.entity.User;

import java.util.List;

/**
 * 认证服务。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public interface AuthService {

    /**
     * 密码连续错误多少次后锁定。
     *
     * @return 次数
     */
    int maxAttempts();

    /**
     * 锁定时长（分钟）。
     *
     * @return 分钟数
     */
    int lockMinutes();

    /**
     * 登录。
     *
     * @param username    登录名
     * @param rawPassword 明文密码
     * @return 登录成功的用户
     */
    User login(String username, String rawPassword);

    /**
     * 管理员手动解锁：清零失败次数并解除锁定。
     *
     * @param username 登录名
     */
    void unlock(String username);

    /**
     * 处于锁定状态或存在失败记录的账号。
     *
     * @return 账号列表
     */
    List<User> lockedAccounts();
}