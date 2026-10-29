package com.lwf.hospital.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务内的工作单元。
 *
 * @param <T> 返回值类型
 * @author LWF-123
 * @since 1.0.0
 */
@FunctionalInterface
public interface SqlWork<T> {

    /**
     * 在事务连接上执行。
     *
     * @param connection 已关闭自动提交的连接
     * @return 结果
     * @throws SQLException SQL 执行失败，触发回滚
     */
    T execute(Connection connection) throws SQLException;
}