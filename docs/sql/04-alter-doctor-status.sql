-- ------------------------------------------------------------------
-- 增量脚本：医生增加“在职/停用”状态
-- 新建库直接执行 01-schema.sql 即可，无需本脚本
-- 执行：mysql -u root -p hospital_queue < docs/sql/04-alter-doctor-status.sql
-- ------------------------------------------------------------------

USE hospital_queue;

ALTER TABLE doctor
    ADD COLUMN enabled TINYINT NOT NULL DEFAULT 1 COMMENT '1=在职 0=停用' AFTER department_id;