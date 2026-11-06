-- 1.科室表：科室id、科室名、房间id（移除挂号数，改为统计查询）
CREATE TABLE department (
    dept_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '科室id',
    dept_name VARCHAR(50) NOT NULL COMMENT '科室名',
    room_id INT NOT NULL COMMENT '房间id',
    is_enable TINYINT DEFAULT 1 COMMENT '是否启用：1启用 0停用'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2.医生表：医生id、医生姓名、医生等级、科室id
CREATE TABLE doctor (
    doc_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '医生id',
    doc_name VARCHAR(20) NOT NULL COMMENT '医生姓名',
    doc_level VARCHAR(20) COMMENT '医生等级',
    dept_id INT NOT NULL COMMENT '科室id',
    is_work TINYINT DEFAULT 1 COMMENT '是否在职：1在职 0离职',
    FOREIGN KEY (dept_id) REFERENCES department(dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3.排班表：增加schedule_id主键，周一~周日，0休息1出诊
CREATE TABLE schedule (
    schedule_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '排班id',
    doc_id INT NOT NULL COMMENT '医生id',
    monday TINYINT DEFAULT 0 COMMENT '周一 0休息 1出诊',
    tuesday TINYINT DEFAULT 0 COMMENT '周二',
    wednesday TINYINT DEFAULT 0 COMMENT '周三',
    thursday TINYINT DEFAULT 0 COMMENT '周四',
    friday TINYINT DEFAULT 0 COMMENT '周五',
    saturday TINYINT DEFAULT 0 COMMENT '周六',
    sunday TINYINT DEFAULT 0 COMMENT '周日',
    max_num INT DEFAULT 20 COMMENT '当日最大号源数',
    FOREIGN KEY (doc_id) REFERENCES doctor(doc_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4.挂号表：重点改动！不再存科室名、医生姓名、用户名文本，改用id关联
CREATE TABLE register (
    reg_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '挂号id',
    user_id INT NOT NULL COMMENT '用户id',
    dept_id INT NOT NULL COMMENT '科室id',
    doc_id INT NOT NULL COMMENT '医生id',
    room_no VARCHAR(30) NOT NULL COMMENT '具体楼层房间号',
    visit_date DATE NOT NULL COMMENT '就诊日期',
    reg_time DATETIME DEFAULT NOW() COMMENT '挂号时间',
    reg_status TINYINT DEFAULT 1 COMMENT '挂号状态：1待就诊 2已就诊 3已取消 4已过期',
    is_pay TINYINT DEFAULT 0 COMMENT '是否缴费：0未缴费，1已缴费',
    FOREIGN KEY (user_id) REFERENCES user_info(user_id),
    FOREIGN KEY (dept_id) REFERENCES department(dept_id),
    FOREIGN KEY (doc_id) REFERENCES doctor(doc_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5.用户注册表：用户id、账号、密码，新增用户姓名、创建时间
CREATE TABLE user_info (
    user_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户id',
    account VARCHAR(30) NOT NULL UNIQUE COMMENT '账号',
    password VARCHAR(100) NOT NULL COMMENT '密码（建议加密存储）',
    user_name VARCHAR(20) NOT NULL COMMENT '用户姓名',
    create_time DATETIME DEFAULT NOW() COMMENT '注册时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--数据添加
--科室表
INSERT INTO department(dept_name, room_id, is_enable)
VALUES
('内科', 1, 1),
('外科', 2, 1),
('儿科', 3, 1),
('妇产科', 4, 1),
('骨科', 5, 1),
('眼科', 6, 1),
('耳鼻喉科', 7, 1),
('皮肤科', 8, 1),
('口腔科', 9, 1),
('神经内科', 10, 1);

--医生表
INSERT INTO doctor(doc_name, doc_level, dept_id, is_work)
VALUES
-- dept_id=1 内科
('任粤','住院医师',1,1),
('刘梅','主治医师',1,1),
-- dept_id=2 外科
('李炜峰','副主任医师',2,1),
('周亮','医士',2,1),
-- dept_id=3 儿科
('李帅','主任医师',3,1),
('吴丹','住院医师',3,1),
-- dept_id=4 妇产科
('卢作成','主治医师',4,1),
('黄静','副主任医师',4,1),
-- dept_id=5 骨科
('赵峰','医士',5,1),
('孙涛','主任医师',5,1),
-- dept_id=6 眼科
('郑芸','住院医师',6,1),
('徐帆','主治医师',6,1),
-- dept_id=7 耳鼻喉科
('朱明','副主任医师',7,1),
('高雯','医士',7,1),
-- dept_id=8 皮肤科
('方婷','主任医师',8,1),
('江华','住院医师',8,1),
-- dept_id=9 口腔科
('肖航','主治医师',9,1),
('罗敏','副主任医师',9,1),
-- dept_id=10 神经内科
('彭博','医士',10,1),
('程雪','主任医师',10,1);

--排班表
INSERT INTO schedule(doc_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,max_num)
VALUES
-- 奇数id：值、休、值、休、值、休、值
(1,1,0,1,0,1,0,1,20),
(3,1,0,1,0,1,0,1,20),
(5,1,0,1,0,1,0,1,20),
(7,1,0,1,0,1,0,1,20),
(9,1,0,1,0,1,0,1,20),
(11,1,0,1,0,1,0,1,20),
(13,1,0,1,0,1,0,1,20),
(15,1,0,1,0,1,0,1,20),
(17,1,0,1,0,1,0,1,20),
(19,1,0,1,0,1,0,1,20),
-- 偶数id：休、值、休、值、休、值、休
(2,0,1,0,1,0,1,0,20),
(4,0,1,0,1,0,1,0,20),
(6,0,1,0,1,0,1,0,20),
(8,0,1,0,1,0,1,0,20),
(10,0,1,0,1,0,1,0,20),
(12,0,1,0,1,0,1,0,20),
(14,0,1,0,1,0,1,0,20),
(16,0,1,0,1,0,1,0,20),
(18,0,1,0,1,0,1,0,20),
(20,0,1,0,1,0,1,0,20);
