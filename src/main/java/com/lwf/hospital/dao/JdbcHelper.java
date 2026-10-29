package com.lwf.hospital.dao;

import com.lwf.hospital.common.exception.DataAccessException;
import com.lwf.hospital.config.DbConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 极简 JDBC 工具：统一连接获取、参数绑定、结果映射与事务边界。
 *
 * <ul>
 *   <li>SQL 只出现在 DAO 实现里，service 不接触 {@code java.sql}；</li>
 *   <li>{@link SQLException} 统一包装为 {@link DataAccessException}；</li>
 *   <li>{@link #inTransaction(SqlWork)} 提供“要么全成功、要么全回滚”的边界。</li>
 * </ul>
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class JdbcHelper {

    private final DbConfig config;

    public JdbcHelper(DbConfig config) {
        this.config = config;
    }

    /**
     * 查询列表（自动开关连接）。
     *
     * @param sql    SQL
     * @param mapper 行映射器
     * @param params 参数
     * @param <T>    元素类型
     * @return 结果列表
     */
    public <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) {
        try (Connection connection = config.open()) {
            return query(connection, sql, mapper, params);
        } catch (SQLException e) {
            throw new DataAccessException("关闭连接失败", e);
        }
    }

    /**
     * 查询列表（使用调用方给定的连接）。
     *
     * @param connection 连接
     * @param sql        SQL
     * @param mapper     行映射器
     * @param params     参数
     * @param <T>        元素类型
     * @return 结果列表
     */
    public <T> List<T> query(Connection connection, String sql, RowMapper<T> mapper, Object... params) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, params);
            try (ResultSet rs = statement.executeQuery()) {
                List<T> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(mapper.map(rs));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DataAccessException("查询失败：" + e.getMessage(), e);
        }
    }

    /**
     * 查询单条（自动开关连接）。
     *
     * @param sql    SQL
     * @param mapper 行映射器
     * @param params 参数
     * @param <T>    元素类型
     * @return 结果
     */
    public <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... params) {
        List<T> list = query(sql, mapper, params);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    /**
     * 查询单条（使用调用方给定的连接）。
     *
     * @param connection 连接
     * @param sql        SQL
     * @param mapper     行映射器
     * @param params     参数
     * @param <T>        元素类型
     * @return 结果
     */
    public <T> Optional<T> queryOne(Connection connection, String sql, RowMapper<T> mapper, Object... params) {
        List<T> list = query(connection, sql, mapper, params);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    /**
     * 执行更新（自动开关连接）。
     *
     * @param sql    SQL
     * @param params 参数
     * @return 受影响行数
     */
    public int update(String sql, Object... params) {
        try (Connection connection = config.open()) {
            return update(connection, sql, params);
        } catch (SQLException e) {
            throw new DataAccessException("关闭连接失败", e);
        }
    }

    /**
     * 执行更新（使用调用方给定的连接）。
     *
     * @param connection 连接
     * @param sql        SQL
     * @param params     参数
     * @return 受影响行数
     */
    public int update(Connection connection, String sql, Object... params) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, params);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("更新失败：" + e.getMessage(), e);
        }
    }

    /**
     * 插入并返回自增主键。
     *
     * @param connection 连接
     * @param sql        SQL
     * @param params     参数
     * @return 自增主键，取不到时返回 0
     */
    public long insert(Connection connection, String sql, Object... params) {
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(statement, params);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getLong(1) : 0L;
            }
        } catch (SQLException e) {
            throw new DataAccessException("插入失败：" + e.getMessage(), e);
        }
    }

    /**
     * 在事务中执行：正常返回提交，抛异常回滚。
     *
     * @param work 工作单元
     * @param <T>  返回值类型
     * @return 工作单元返回值
     */
    public <T> T inTransaction(SqlWork<T> work) {
        try (Connection connection = config.open()) {
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                T result = work.execute(connection);
                connection.commit();
                return result;
            } catch (SQLException | RuntimeException e) {
                safeRollback(connection);
                if (e instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                }
                throw new DataAccessException("事务执行失败：" + e.getMessage(), e);
            } finally {
                try {
                    connection.setAutoCommit(originalAutoCommit);
                } catch (SQLException ignored) {
                    // 连接即将关闭，忽略
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("事务连接异常：" + e.getMessage(), e);
        }
    }

    private void safeRollback(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
            // 回滚失败时保留原始异常
        }
    }

    private void bind(PreparedStatement statement, Object... params) throws SQLException {
        if (params == null) {
            return;
        }
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
    }
}