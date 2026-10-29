package com.lwf.hospital.service;

import com.lwf.hospital.domain.entity.User;

/**
 * 认证服务。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public interface AuthService {

    /**
     * 登录。
     *
     * @param username    登录名
     * @param rawPassword 明文密码
     * @return 登录成功的用户
     */
    User login(String username, String rawPassword);
}