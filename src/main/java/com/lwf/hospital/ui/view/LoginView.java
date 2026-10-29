package com.lwf.hospital.ui.view;

import com.lwf.hospital.common.exception.AppException;
import com.lwf.hospital.common.log.AppLogger;
import com.lwf.hospital.common.util.InputUtils;
import com.lwf.hospital.domain.entity.User;
import com.lwf.hospital.service.AuthService;

/**
 * 登录界面：失败可重试，输入 0 退出系统。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class LoginView {

    private final AuthService authService;

    public LoginView(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 循环登录，直到成功或用户选择退出。
     *
     * @return 登录成功的用户；退出返回 null
     */
    public User login() {
        while (true) {
            System.out.println();
            System.out.println("  ─── 用户登录（输入 0 退出系统）───");
            String username = InputUtils.readLine("  登录名：");
            if ("0".equals(username)) {
                return null;
            }
            String password = InputUtils.readPassword("  密  码：");
            try {
                User user = authService.login(username, password);
                System.out.println("  ✓ 登录成功，欢迎 " + user.getRealName()
                        + "（" + user.getRole().getLabel() + "）");
                return user;
            } catch (AppException e) {
                // 登录失败属于可预期错误：提示后继续
                System.out.println("  ✗ " + e.getMessage());
                AppLogger.warn("登录失败：" + username + " - " + e.getMessage());
            }
        }
    }
}