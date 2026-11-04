package com.lwf.hospital.dao.impl;

import com.lwf.hospital.dao.JdbcHelper;
import com.lwf.hospital.dao.UserDao;
import com.lwf.hospital.domain.entity.User;
import com.lwf.hospital.domain.enums.RoleType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * 用户 DAO 的 JDBC 实现。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class UserDaoImpl implements UserDao {

    private static final String COLUMNS = """
            SELECT id, username, password_hash, salt, role, ref_id, real_name, enabled,
                   failed_attempts, locked_until
            FROM sys_user
            """;

    private final JdbcHelper jdbc;

    public UserDaoImpl(JdbcHelper jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jdbc.queryOne(COLUMNS + " WHERE username = ? AND enabled = 1", UserDaoImpl::map, username);
    }

    @Override
    public int increaseFailedAttempts(String username, int maxAttempts, int lockMinutes) {
        return jdbc.update("""
                        UPDATE sys_user
                        SET locked_until = IF(failed_attempts + 1 >= ?,
                                              DATE_ADD(NOW(), INTERVAL ? MINUTE),
                                              locked_until),
                            failed_attempts = failed_attempts + 1
                        WHERE username = ?
                        """, maxAttempts, lockMinutes, username);
    }

    @Override
    public int resetFailedAttempts(String username) {
        return jdbc.update(
                "UPDATE sys_user SET failed_attempts = 0, locked_until = NULL WHERE username = ?", username);
    }

    @Override
    public List<User> findLockedOrFailed() {
        return jdbc.query(COLUMNS
                        + " WHERE locked_until IS NOT NULL OR failed_attempts > 0"
                        + " ORDER BY locked_until DESC, failed_attempts DESC",
                UserDaoImpl::map);
    }

    private static User map(ResultSet rs) throws SQLException {
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
        user.setFailedAttempts(rs.getInt("failed_attempts"));
        Timestamp lockedUntil = rs.getTimestamp("locked_until");
        user.setLockedUntil(lockedUntil == null ? null : lockedUntil.toLocalDateTime());
        return user;
    }
}