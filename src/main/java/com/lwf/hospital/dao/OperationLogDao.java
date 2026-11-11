package com.lwf.hospital.dao;

/**
 * 操作日志数据访问接口。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public interface OperationLogDao {

    /**
     * 写一条操作日志。
     *
     * @param userId 操作人 id，可为 null
     * @param action 动作，如 ADD_QUOTA
     * @param detail 详情
     * @return 日志 id
     */
    long insert(Long userId, String action, String detail);
}