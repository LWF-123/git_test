-- ==================================================================
-- 门诊挂号与排队叫号系统 —— 建表脚本
-- 数据库：MySQL 8.0+
-- 执行：mysql -u root -p < 01-schema.sql
-- ==================================================================

DROP DATABASE IF EXISTS hospital_queue;
CREATE DATABASE hospital_queue
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;
USE hospital_queue;

-- ------------------------------------------------------------------
-- 1. 科室
-- ------------------------------------------------------------------
CREATE TABLE department (
    id         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    dept_code  VARCHAR(20) NOT NULL COMMENT '科室编码',
    name       VARCHAR(50) NOT NULL COMMENT '科室名称',
    location   VARCHAR(50) NOT NULL DEFAULT '' COMMENT '位置，如 门诊楼 2 层',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dept_code (dept_code)
) ENGINE = InnoDB COMMENT '科室';

-- ------------------------------------------------------------------
-- 2. 医生
-- ------------------------------------------------------------------
CREATE TABLE doctor (
    id            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    doctor_no     VARCHAR(20) NOT NULL COMMENT '工号',
    name          VARCHAR(50) NOT NULL COMMENT '姓名',
    title         VARCHAR(30) NOT NULL DEFAULT '医师' COMMENT '职称',
    department_id BIGINT      NOT NULL COMMENT '所属科室',
    PRIMARY KEY (id),
    UNIQUE KEY uk_doctor_no (doctor_no),
    KEY idx_doctor_dept (department_id),
    CONSTRAINT fk_doctor_dept FOREIGN KEY (department_id) REFERENCES department (id)
) ENGINE = InnoDB COMMENT '医生';

-- ------------------------------------------------------------------
-- 3. 患者
-- ------------------------------------------------------------------
CREATE TABLE patient (
    id         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    patient_no VARCHAR(20) NOT NULL COMMENT '就诊卡号',
    name       VARCHAR(50) NOT NULL COMMENT '姓名',
    gender     VARCHAR(4)  NOT NULL DEFAULT '男' COMMENT '性别',
    phone      VARCHAR(20) NOT NULL DEFAULT '' COMMENT '联系电话',
    PRIMARY KEY (id),
    UNIQUE KEY uk_patient_no (patient_no)
) ENGINE = InnoDB COMMENT '患者';

-- ------------------------------------------------------------------
-- 4. 排班（号源）：某医生某天上午/下午放多少个号
-- ------------------------------------------------------------------
CREATE TABLE doctor_schedule (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    doctor_id   BIGINT      NOT NULL COMMENT '医生 id',
    work_date   DATE        NOT NULL COMMENT '出诊日期',
    period      VARCHAR(2)  NOT NULL COMMENT 'AM=上午 PM=下午',
    quota_total INT         NOT NULL DEFAULT 20 COMMENT '总号源',
    quota_used  INT         NOT NULL DEFAULT 0 COMMENT '已用号源',
    status      VARCHAR(10) NOT NULL DEFAULT 'OPEN' COMMENT 'OPEN/CLOSED',
    PRIMARY KEY (id),
    UNIQUE KEY uk_schedule (doctor_id, work_date, period),
    KEY idx_schedule_date (work_date),
    CONSTRAINT fk_schedule_doctor FOREIGN KEY (doctor_id) REFERENCES doctor (id)
) ENGINE = InnoDB COMMENT '医生排班与号源';

-- ------------------------------------------------------------------
-- 5. 挂号单
-- ------------------------------------------------------------------
CREATE TABLE registration (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    patient_id  BIGINT       NOT NULL COMMENT '患者 id',
    schedule_id BIGINT       NOT NULL COMMENT '排班 id',
    reg_type    VARCHAR(20)  NOT NULL COMMENT 'NORMAL/EXPERT/EMERGENCY',
    queue_no    INT          NOT NULL COMMENT '排队号',
    priority    INT          NOT NULL DEFAULT 20 COMMENT '优先级，越小越先叫',
    fee         DECIMAL(6,2) NOT NULL DEFAULT 0 COMMENT '挂号费',
    status      VARCHAR(20)  NOT NULL DEFAULT 'WAITING' COMMENT 'WAITING/CALLING/SERVED/SKIPPED/CANCELLED',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '挂号时间',
    PRIMARY KEY (id),
    KEY idx_reg_schedule (schedule_id),
    KEY idx_reg_patient (patient_id),
    CONSTRAINT fk_reg_patient FOREIGN KEY (patient_id) REFERENCES patient (id),
    CONSTRAINT fk_reg_schedule FOREIGN KEY (schedule_id) REFERENCES doctor_schedule (id)
) ENGINE = InnoDB COMMENT '挂号单';

-- ------------------------------------------------------------------
-- 6. 叫号队列（与挂号单一一对应，单独成表便于按优先级取号）
-- ------------------------------------------------------------------
CREATE TABLE queue_ticket (
    id              BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    registration_id BIGINT      NOT NULL COMMENT '挂号单 id',
    schedule_id     BIGINT      NOT NULL COMMENT '排班 id（一次排班一条队列）',
    queue_no        INT         NOT NULL COMMENT '排队号',
    priority        INT         NOT NULL COMMENT '优先级，越小越先叫',
    status          VARCHAR(20) NOT NULL DEFAULT 'WAITING' COMMENT 'WAITING/CALLING/SERVED/SKIPPED',
    created_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入队时间',
    called_at       DATETIME    NULL COMMENT '叫号时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_queue_registration (registration_id),
    KEY idx_queue_schedule (schedule_id, status, priority, queue_no),
    CONSTRAINT fk_queue_registration FOREIGN KEY (registration_id) REFERENCES registration (id) ON DELETE CASCADE,
    CONSTRAINT fk_queue_schedule FOREIGN KEY (schedule_id) REFERENCES doctor_schedule (id)
) ENGINE = InnoDB COMMENT '叫号队列';

-- ------------------------------------------------------------------
-- 7. 就诊记录（医生接诊后填写，骨架阶段预留）
-- ------------------------------------------------------------------
CREATE TABLE visit_record (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    registration_id BIGINT       NOT NULL COMMENT '挂号单 id',
    chief_complaint VARCHAR(200) NOT NULL DEFAULT '' COMMENT '主诉',
    diagnosis       VARCHAR(200) NOT NULL DEFAULT '' COMMENT '诊断',
    advice          VARCHAR(200) NOT NULL DEFAULT '' COMMENT '医嘱',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_visit_registration (registration_id),
    CONSTRAINT fk_visit_registration FOREIGN KEY (registration_id) REFERENCES registration (id) ON DELETE CASCADE
) ENGINE = InnoDB COMMENT '就诊记录';

-- ------------------------------------------------------------------
-- 8. 系统用户（登录与角色，ref_id 指向患者或医生）
-- ------------------------------------------------------------------
CREATE TABLE sys_user (
    id            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    username      VARCHAR(50) NOT NULL COMMENT '登录名',
    password_hash CHAR(64)    NOT NULL COMMENT '加盐 SHA-256 摘要',
    salt          VARCHAR(32) NOT NULL COMMENT '盐值',
    role          VARCHAR(20) NOT NULL COMMENT 'ADMIN/DOCTOR/PATIENT',
    ref_id        BIGINT      NULL COMMENT '患者 id 或医生 id，管理员为空',
    real_name     VARCHAR(50) NOT NULL COMMENT '真实姓名',
    enabled       TINYINT     NOT NULL DEFAULT 1 COMMENT '1=启用 0=禁用',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_username (username)
) ENGINE = InnoDB COMMENT '系统用户';

-- ------------------------------------------------------------------
-- 9. 操作日志
-- ------------------------------------------------------------------
CREATE TABLE operation_log (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id    BIGINT       NULL COMMENT '操作人',
    action     VARCHAR(50)  NOT NULL COMMENT '动作，如 REGISTER/CALL/SKIP',
    detail     VARCHAR(500) NOT NULL DEFAULT '' COMMENT '详情',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间',
    PRIMARY KEY (id),
    KEY idx_log_time (created_at)
) ENGINE = InnoDB COMMENT '操作日志';