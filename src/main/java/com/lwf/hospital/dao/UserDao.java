package com.lwf.hospital.dao;

import com.lwf.hospital.domain.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public interface UserDao {

    /**
     * 按登录名查询启用的用户。
     *
     * @param username 登录名
     * @return 用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 累加一次登录失败，并在达到阈值时写入锁定截止时间。
     *
     * <p>用一条 SQL 完成“计数 + 判断是否锁定”，避免“先查后写”的竞态：
     * 先按旧值判断是否该锁，再自增计数。</p>
     *
     * @param username    登录名
     * @param maxAttempts 触发锁定的失败次数
     * @param lockMinutes 锁定分钟数
     * @return 受影响行数
     */
    int increaseFailedAttempts(String username, int maxAttempts, int lockMinutes);

    /**
     * 清零失败计数并解除锁定。
     *
     * @param username 登录名
     * @return 受影响行数，0 表示账号不存在
     */
    int resetFailedAttempts(String username);

    /**
     * 查询处于锁定状态或存在失败记录的账号（管理员风控列表）。
     *
     * @return 账号列表
     */
    List<User> findLockedOrFailed();
}