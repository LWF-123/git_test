package com.lwf.hospital.service.impl;

import com.lwf.hospital.common.exception.AuthException;
import com.lwf.hospital.common.exception.ValidationException;
import com.lwf.hospital.common.log.AppLogger;
import com.lwf.hospital.common.util.PasswordUtils;
import com.lwf.hospital.dao.UserDao;
import com.lwf.hospital.domain.entity.User;
import com.lwf.hospital.service.AuthService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 认证服务实现：密码校验 + 登录失败锁定。
 *
 * <p>风控规则：同一账号连续 {@value #MAX_FAILED_ATTEMPTS} 次密码错误锁定
 * {@value #LOCK_MINUTES} 分钟；登录成功或管理员手动解锁都会清零计数。</p>
 *
 * <p>锁定状态存在数据库而不是内存，好处是重启程序、换一台机器登录都绕不过去。</p>
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class AuthServiceImpl implements AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 10;

    private final UserDao userDao;

    public AuthServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public int maxAttempts() {
        return MAX_FAILED_ATTEMPTS;
    }

    @Override
    public int lockMinutes() {
        return LOCK_MINUTES;
    }

    @Override
    public User login(String username, String rawPassword) {
        if (username == null || username.isBlank()) {
            throw new ValidationException("登录名不能为空。");
        }
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new ValidationException("密码不能为空。");
        }
        String name = username.trim();
        User user = userDao.findByUsername(name)
                .orElseThrow(() -> new AuthException("用户名或密码错误。"));

        if (user.isLocked()) {
            long minutes = Math.max(1,
                    Duration.between(LocalDateTime.now(), user.getLockedUntil()).toMinutes() + 1);
            throw new AuthException("账号已被锁定，请 " + minutes + " 分钟后重试，或联系管理员解锁。");
        }
        if (!user.isEnabled()) {
            throw new AuthException("该账号已被禁用，请联系管理员。");
        }
        // 锁定已到期：先清零再校验，避免旧计数影响下一次判断
        if (user.getLockedUntil() != null) {
            userDao.resetFailedAttempts(name);
            user.setFailedAttempts(0);
            user.setLockedUntil(null);
        }

        if (!PasswordUtils.matches(rawPassword, user.getSalt(), user.getPasswordHash())) {
            int attempts = user.getFailedAttempts() + 1;
            userDao.increaseFailedAttempts(name, MAX_FAILED_ATTEMPTS, LOCK_MINUTES);
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                AppLogger.warn("账号锁定：" + name + "（连续失败 " + attempts + " 次）");
                throw new AuthException("密码连续错误 " + MAX_FAILED_ATTEMPTS + " 次，账号已锁定 "
                        + LOCK_MINUTES + " 分钟，可联系管理员解锁。");
            }
            AppLogger.warn("登录失败：" + name + "（第 " + attempts + " 次）");
            int remaining = MAX_FAILED_ATTEMPTS - attempts;
            if (remaining == 1) {
                throw new AuthException("用户名或密码错误，还剩 1 次机会，再输错一次账号将被锁定 "
                        + LOCK_MINUTES + " 分钟。");
            }
            throw new AuthException("用户名或密码错误，还剩 " + remaining + " 次机会。");
        }

        userDao.resetFailedAttempts(name);
        AppLogger.info("登录成功：" + name + "（" + user.getRole().getLabel() + "）");
        return user;
    }

    @Override
    public void unlock(String username) {
        if (username == null || username.isBlank()) {
            throw new ValidationException("登录名不能为空。");
        }
        String name = username.trim();
        if (userDao.resetFailedAttempts(name) == 0) {
            throw new AuthException("账号不存在：" + name);
        }
        AppLogger.info("管理员解锁账号：" + name);
    }

    @Override
    public List<User> lockedAccounts() {
        return userDao.findLockedOrFailed();
    }
}