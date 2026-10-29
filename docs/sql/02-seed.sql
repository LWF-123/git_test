-- ==================================================================
-- 门诊挂号与排队叫号系统 —— 初始化演示数据
-- 依赖：先执行 01-schema.sql
-- 账号：
--   管理员 admin    / admin123
--   医生   d1001    / doctor123
--   患者   p2023001 / 123456
-- ==================================================================
USE hospital_queue;

-- ---------- 科室 ----------
INSERT INTO department (dept_code, name, location) VALUES
('NEI', '内科', '门诊楼 1 层'),
('WAI', '外科', '门诊楼 2 层'),
('ER',  '儿科', '门诊楼 1 层东侧');

-- ---------- 医生 ----------
INSERT INTO doctor (doctor_no, name, title, department_id) VALUES
('d1001', '刘医生', '主任医师',   1),
('d1002', '陈医生', '主治医师',   1),
('d1003', '赵医生', '副主任医师', 2),
('d1004', '周医生', '主治医师',   3);

-- ---------- 患者 ----------
INSERT INTO patient (patient_no, name, gender, phone) VALUES
('p2023001', '张三', '男', '13800000001'),
('p2023002', '李四', '女', '13800000002'),
('p2023003', '王五', '男', '13800000003');

-- ---------- 排班（今天上午/下午 + 明天上午）----------
INSERT INTO doctor_schedule (doctor_id, work_date, period, quota_total, quota_used, status) VALUES
(1, CURDATE(), 'AM', 20, 2, 'OPEN'),
(1, CURDATE(), 'PM', 15, 0, 'OPEN'),
(2, CURDATE(), 'AM', 10, 0, 'OPEN'),
(3, CURDATE(), 'AM', 20, 0, 'OPEN'),
(4, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AM', 25, 0, 'OPEN'),
(2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'PM', 10, 0, 'CLOSED');

-- ---------- 挂号单与叫号队列（演示“急诊优先”）----------
-- 李四挂普通号，取号 1；王五挂急诊号，取号 2；急诊应排在普通号之前被叫到
INSERT INTO registration (patient_id, schedule_id, reg_type, queue_no, priority, fee, status) VALUES
(2, 1, 'NORMAL',    1, 20, 10.00, 'WAITING'),
(3, 1, 'EMERGENCY', 2, 10, 50.00, 'WAITING');

INSERT INTO queue_ticket (registration_id, schedule_id, queue_no, priority, status) VALUES
(1, 1, 1, 20, 'WAITING'),
(2, 1, 2, 10, 'WAITING');

-- ---------- 系统用户 ----------
INSERT INTO sys_user (username, password_hash, salt, role, ref_id, real_name, enabled) VALUES
('admin',    '4d3a5c8068865de2af7fa7e9a79c0bf74ab5396a0edfcfc1a59f67b1c5243938', 's-admin',   'ADMIN',   NULL, '系统管理员', 1),
('d1001',    '2abda76062e19adf04659d24268fb8624de66e439702c27084a7c0f419f07bd0', 's-doctor',  'DOCTOR',  1,    '刘医生',     1),
('p2023001', 'bb34f0b2d8eb19932a0e103c4d077e054630ca7086444985bd9df5d8915a4a37', 's-patient', 'PATIENT', 1,    '张三',       1),
('p2023002', 'bb34f0b2d8eb19932a0e103c4d077e054630ca7086444985bd9df5d8915a4a37', 's-patient', 'PATIENT', 2,    '李四',       1);