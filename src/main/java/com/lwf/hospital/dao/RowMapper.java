package com.lwf.hospital.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 行映射器：把 {@link ResultSet} 的一行转换成领域对象。
 *
 * @param <T> 目标类型
 * @author LWF-123
 * @since 1.0.0
 */
@FunctionalInterface
public interface RowMapper<T> {

    /**
     * 映射一行。
     *
     * @param rs 结果集，游标已指向当前行
     * @return 领域对象
     * @throws SQLException 读取字段失败
     */
    T map(ResultSet rs) throws SQLException;
}