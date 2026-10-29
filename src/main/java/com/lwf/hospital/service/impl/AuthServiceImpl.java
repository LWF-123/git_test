package com.lwf.hospital.service.impl;

import com.lwf.hospital.common.exception.AuthException;
import com.lwf.hospital.common.exception.ValidationException;
import com.lwf.hospital.common.log.AppLogger;
import com.lwf.hospital.common.util.PasswordUtils;
import com.lwf.hospital.dao.UserDao;
import com.lwf.hospital.domain.entity.User;
import com.lwf.hospital.service.AuthService;

/**
 * 认证服务实现。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;

    public AuthServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User login(String username, String rawPassword) {
        if (username == null || username.isBlank()) {
            throw new ValidationException("登录名不能为空。");
        }
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new ValidationException("密码不能为空。");
        }
        User user = userDao.findByUsername(username.trim())
                .orElseThrow(() -> new AuthException("用户名或密码错误。"));
        if (!user.isEnabled()) {
            throw new AuthException("该账号已被禁用，请联系管理员。");
        }
        if (!PasswordUtils.matches(rawPassword, user.getSalt(), user.getPasswordHash())) {
            AppLogger.warn("登录失败，密码错误：" + username);
            throw new AuthException("用户名或密码错误。");
        }
        AppLogger.info("登录成功：" + username + "（" + user.getRole().getLabel() + "）");
        return user;
    }
}