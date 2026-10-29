# 门诊挂号与排队叫号系统（Java SE 控制台 + MySQL）

课程设计项目：纯 Java SE 控制台应用，使用 MySQL 持久化，覆盖面向对象设计、集合、异常处理、多线程与并发控制等知识点。

## 技术栈

| 类别 | 选型 | 说明 |
|---|---|---|
| 语言 / 运行时 | Java 17 | record、文本块、switch 表达式 |
| 构建 | Maven（主）/ 纯 javac 脚本（备） | 无网络环境可用 `scripts/build.ps1` |
| 数据库 | MySQL 8.0+ | JDBC 直连，手写 DAO，不依赖 ORM |
| 界面 | 控制台 | 无任何前端技术 |
| 日志 | java.util.logging | 零第三方依赖，输出到 `logs/` |

## 功能范围（骨架版）

| 角色 | 已实现 | 后续扩展 |
|---|---|---|
| 患者 | 科室列表、号源查询、挂号（普通/专家/急诊）、我的挂号 | 退号、预约、就诊记录查询 |
| 医生 | 选择今日排班、候诊队列、叫下一位、过号、完成接诊 | 填写病历、开处方 |
| 管理员 | 科室列表、号源总览 | 医生与排班维护、挂号统计报表 |

## 快速开始

```bash
# 1. 建库与初始化数据
mysql -u root -p < docs/sql/01-schema.sql
mysql -u root -p < docs/sql/02-seed.sql

# 2. 配置连接：复制模板并改成自己的账号密码（该文件不会被提交）
#    src/main/resources/db.properties.example -> db.properties

# 3. 运行
mvn -q clean package && java -jar target/hospital-queue-system.jar
# 没有 Maven 时：
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/run.ps1
```

没有 Maven 时需要把 `mysql-connector-j-*.jar` 放进 `lib/`。

### 演示账号

| 角色 | 登录名 | 密码 |
|---|---|---|
| 管理员 | `admin` | `admin123` |
| 医生 | `d1001` | `doctor123` |
| 患者 | `p2023001` | `123456` |

## 目录结构

```
.
├── pom.xml
├── docs/
│   ├── 01-项目说明.md
│   └── sql/                  # 建表与演示数据
├── scripts/                  # 无 Maven 时的构建/运行脚本
└── src/main/
    ├── java/com/lwf/hospital/
    │   ├── Main.java         # 只做启动与装配
    │   ├── config/           # 配置加载
    │   ├── context/          # 简易依赖注入
    │   ├── common/           # 异常、日志、工具
    │   ├── domain/           # 实体、枚举、视图对象
    │   ├── dao/              # JDBC 工具 + DAO 接口与实现
    │   ├── service/          # 认证、挂号、叫号、规则链
    │   └── ui/               # 控制台界面
    └── resources/db.properties.example
```

## 设计要点

1. **分层**：`ui → service → dao → domain`，界面不含 SQL，`Main` 不含业务。
2. **开闭原则**：挂号校验是责任链（`RegistrationRule`），新增规则只加一个类。
3. **并发安全**：`SELECT ... FOR UPDATE` 锁排班 + `UPDATE ... WHERE quota_used < quota_total` 条件更新 + 事务，杜绝超号。
4. **叫号不重复**：叫下一位时锁定队首（`FOR UPDATE`），两人同时点也只会叫到不同患者。
5. **优先级队列**：急诊（10）&lt; 专家（15）&lt; 普通（20），同优先级按排队号先后。
6. **异常分级**：可提示异常与系统错误分开处理，全局兜底不崩界面。

## 文档

- 需求、架构、表结构与扩展点：[docs/01-项目说明.md](docs/01-项目说明.md)