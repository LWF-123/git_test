package com.lwf.hospital.dao;

import com.lwf.hospital.domain.entity.User;

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
}