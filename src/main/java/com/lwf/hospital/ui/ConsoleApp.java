package com.lwf.hospital.ui;

import com.lwf.hospital.context.AppContext;
import com.lwf.hospital.domain.entity.User;
import com.lwf.hospital.ui.view.AdminView;
import com.lwf.hospital.ui.view.DoctorView;
import com.lwf.hospital.ui.view.LoginView;
import com.lwf.hospital.ui.view.PatientView;

/**
 * 控制台主程序：启动画面 → 登录 → 按角色分发。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class ConsoleApp {

    private final LoginView loginView;
    private final PatientView patientView;
    private final DoctorView doctorView;
    private final AdminView adminView;

    public ConsoleApp(AppContext context) {
        this.loginView = new LoginView(context.authService());
        this.patientView = new PatientView(context.registrationService());
        this.doctorView = new DoctorView(context.queueService(), context.scheduleDao());
        this.adminView = new AdminView(context.registrationService());
    }

    /**
     * 启动主循环。
     */
    public void start() {
        printBanner();
        while (true) {
            User user = loginView.login();
            if (user == null) {
                System.out.println();
                System.out.println("  已退出系统，感谢使用。");
                return;
            }
            switch (user.getRole()) {
                case PATIENT -> patientView.show(user);
                case DOCTOR -> doctorView.show(user);
                case ADMIN -> adminView.show(user);
                default -> throw new IllegalStateException("未知角色：" + user.getRole());
            }
            System.out.println();
            System.out.println("  已退出登录。");
        }
    }

    private void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════╗");
        System.out.println("  ║      门诊挂号与排队叫号系统  v1.0.0          ║");
        System.out.println("  ║        Java SE 17  ·  MySQL  ·  控制台版     ║");
        System.out.println("  ╚══════════════════════════════════════════════╝");
        System.out.println("  演示账号：admin/admin123   d1001/doctor123   p2023001/123456");
        System.out.println("  说明：首次运行请先执行 docs/sql 下的建表与初始化脚本。");
    }
}