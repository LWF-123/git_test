package com.lwf.hospital.context;

import com.lwf.hospital.common.log.AppLogger;
import com.lwf.hospital.config.DbConfig;
import com.lwf.hospital.dao.DepartmentDao;
import com.lwf.hospital.dao.DoctorDao;
import com.lwf.hospital.dao.JdbcHelper;
import com.lwf.hospital.dao.QueueDao;
import com.lwf.hospital.dao.RegistrationDao;
import com.lwf.hospital.dao.ScheduleDao;
import com.lwf.hospital.dao.UserDao;
import com.lwf.hospital.dao.impl.DepartmentDaoImpl;
import com.lwf.hospital.dao.impl.DoctorDaoImpl;
import com.lwf.hospital.dao.impl.QueueDaoImpl;
import com.lwf.hospital.dao.impl.RegistrationDaoImpl;
import com.lwf.hospital.dao.impl.ScheduleDaoImpl;
import com.lwf.hospital.dao.impl.UserDaoImpl;
import com.lwf.hospital.service.AuthService;
import com.lwf.hospital.service.DepartmentService;
import com.lwf.hospital.service.DoctorService;
import com.lwf.hospital.service.QueueService;
import com.lwf.hospital.service.RegistrationService;
import com.lwf.hospital.service.impl.AuthServiceImpl;
import com.lwf.hospital.service.impl.DepartmentServiceImpl;
import com.lwf.hospital.service.impl.DoctorServiceImpl;
import com.lwf.hospital.service.impl.QueueServiceImpl;
import com.lwf.hospital.service.impl.RegistrationServiceImpl;

/**
 * 应用上下文：集中负责对象装配（简易版依赖注入）。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public final class AppContext {

    private final DbConfig dbConfig;
    private final JdbcHelper jdbc;
    private final UserDao userDao;
    private final ScheduleDao scheduleDao;
    private final RegistrationDao registrationDao;
    private final QueueDao queueDao;
    private final DepartmentDao departmentDao;
    private final DoctorDao doctorDao;
    private final AuthService authService;
    private final RegistrationServiceImpl registrationService;
    private final QueueService queueService;
    private final DepartmentService departmentService;
    private final DoctorService doctorService;

    private AppContext() {
        this.dbConfig = DbConfig.load();
        this.jdbc = new JdbcHelper(dbConfig);

        this.userDao = new UserDaoImpl(jdbc);
        this.scheduleDao = new ScheduleDaoImpl(jdbc);
        this.registrationDao = new RegistrationDaoImpl(jdbc);
        this.queueDao = new QueueDaoImpl(jdbc);
        this.departmentDao = new DepartmentDaoImpl(jdbc);
        this.doctorDao = new DoctorDaoImpl(jdbc);

        this.authService = new AuthServiceImpl(userDao);
        this.registrationService = new RegistrationServiceImpl(jdbc, scheduleDao, registrationDao, queueDao);
        this.queueService = new QueueServiceImpl(jdbc, registrationDao, queueDao);
        this.departmentService = new DepartmentServiceImpl(departmentDao);
        this.doctorService = new DoctorServiceImpl(jdbc, doctorDao, departmentDao, scheduleDao);
    }

    /**
     * 启动装配。
     *
     * @return 已装配好的上下文
     */
    public static AppContext bootstrap() {
        AppContext context = new AppContext();
        AppLogger.info("数据库：" + context.dbConfig.getUrl() + "，用户：" + context.dbConfig.getUser());
        AppLogger.info("挂号规则链：" + context.registrationService.ruleNames());
        return context;
    }

    public AuthService authService() {
        return authService;
    }

    public RegistrationService registrationService() {
        return registrationService;
    }

    public QueueService queueService() {
        return queueService;
    }

    public DepartmentService departmentService() {
        return departmentService;
    }

    public DoctorService doctorService() {
        return doctorService;
    }

    public ScheduleDao scheduleDao() {
        return scheduleDao;
    }

    public UserDao userDao() {
        return userDao;
    }

    public JdbcHelper jdbc() {
        return jdbc;
    }
}