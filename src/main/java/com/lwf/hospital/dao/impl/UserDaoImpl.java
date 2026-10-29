package com.lwf.hospital.dao.impl;

import com.lwf.hospital.dao.JdbcHelper;
import com.lwf.hospital.dao.UserDao;
import com.lwf.hospital.domain.entity.User;
import com.lwf.hospital.domain.enums.RoleType;

import java.util.Optional;

/**
 * 用户 DAO 的 JDBC 实现。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class UserDaoImpl implements UserDao {

    private static final String SQL_FIND_BY_USERNAME = """
            SELECT id, username, password_hash, salt, role, ref_id, real_name, enabled
            FROM sys_user
            WHERE username = ? AND enabled = 1
            """;

    private final JdbcHelper jdbc;

    public UserDaoImpl(JdbcHelper jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jdbc.queryOne(SQL_FIND_BY_USERNAME, rs -> {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setSalt(rs.getString("salt"));
            user.setRole(RoleType.fromCode(rs.getString("role")));
            long refId = rs.getLong("ref_id");
            user.setRefId(rs.wasNull() ? null : refId);
            user.setRealName(rs.getString("real_name"));
            user.setEnabled(rs.getInt("enabled") == 1);
            return user;
        }, username);
    }
}