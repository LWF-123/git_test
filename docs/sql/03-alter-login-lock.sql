-- ------------------------------------------------------------------
-- 增量脚本：为已存在的数据库补充“登录失败锁定”字段
-- 新建库直接执行 01-schema.sql 即可，无需本脚本
-- 执行：mysql -u root -p hospital_queue < docs/sql/03-alter-login-lock.sql
-- ------------------------------------------------------------------

USE hospital_queue;

ALTER TABLE sys_user
    ADD COLUMN failed_attempts INT NOT NULL DEFAULT 0 COMMENT '连续登录失败次数' AFTER enabled,
    ADD COLUMN locked_until DATETIME NULL COMMENT '锁定截止时间，NULL 表示未锁定' AFTER failed_attempts;

-- 解锁某个账号（管理员界面之外的手工兜底方案）
-- UPDATE sys_user SET failed_attempts = 0, locked_until = NULL WHERE username = 'p2023002';