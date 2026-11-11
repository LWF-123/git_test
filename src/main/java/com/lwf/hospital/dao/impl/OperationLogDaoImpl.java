package com.lwf.hospital.dao.impl;

import com.lwf.hospital.dao.JdbcHelper;
import com.lwf.hospital.dao.OperationLogDao;

/**
 * 操作日志 DAO 的 JDBC 实现。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class OperationLogDaoImpl implements OperationLogDao {

    private final JdbcHelper jdbc;

    public OperationLogDaoImpl(JdbcHelper jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public long insert(Long userId, String action, String detail) {
        return jdbc.insert("INSERT INTO operation_log (user_id, action, detail) VALUES (?, ?, ?)",
                userId, action, detail);
    }
}