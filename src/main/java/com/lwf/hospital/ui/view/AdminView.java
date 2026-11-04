package com.lwf.hospital.ui.view;

import com.lwf.hospital.common.util.ConsoleTable;
import com.lwf.hospital.common.util.InputUtils;
import com.lwf.hospital.domain.dto.ScheduleView;
import com.lwf.hospital.domain.entity.Department;
import com.lwf.hospital.domain.entity.User;
import com.lwf.hospital.service.AuthService;
import com.lwf.hospital.service.RegistrationService;
import com.lwf.hospital.ui.component.Menu;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员端：科室与号源总览、账号锁定管理（排班维护、统计报表为后续扩展点）。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class AdminView {

    private static final List<String> DEPT_HEADERS = List.of("ID", "科室编码", "科室名称", "位置");
    private static final List<String> SCHEDULE_HEADERS = List.of(
            "ID", "科室", "医生", "职称", "日期", "时段", "已挂/总量", "状态");
    private static final List<String> LOCK_HEADERS = List.of(
            "登录名", "姓名", "角色", "失败次数", "状态");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("MM-dd HH:mm");

    private final RegistrationService registrationService;
    private final AuthService authService;

    public AdminView(RegistrationService registrationService, AuthService authService) {
        this.registrationService = registrationService;
        this.authService = authService;
    }

    /**
     * 展示管理员菜单，直到返回。
     *
     * @param user 当前登录用户
     */
    public void show(User user) {
        Menu menu = new Menu("管理员菜单 · " + user.getRealName());
        menu.add(1, "科室列表", this::printDepartments)
                .add(2, "号源总览", () -> printSchedules(registrationService.listAvailableSchedules(0)))
                .add(3, "医生与排班维护（待实现）", () -> System.out.println("  该功能为后续扩展点，见 docs/01-项目说明.md"))
                .add(4, "挂号统计报表（待实现）", () -> System.out.println("  该功能为后续扩展点，见 docs/01-项目说明.md"))
                .add(5, "账号锁定与解锁", this::manageLocks);
        menu.run();
    }

    private void printDepartments() {
        List<Department> departments = registrationService.listDepartments();
        List<List<String>> rows = new ArrayList<>();
        for (Department department : departments) {
            rows.add(List.of(String.valueOf(department.getId()), department.getDeptCode(),
                    department.getName(), department.getLocation()));
        }
        ConsoleTable.print(DEPT_HEADERS, rows);
    }

    private void printSchedules(List<ScheduleView> schedules) {
        List<List<String>> rows = new ArrayList<>();
        for (ScheduleView schedule : schedules) {
            rows.add(List.of(
                    String.valueOf(schedule.scheduleId()),
                    schedule.deptName(),
                    schedule.doctorName(),
                    schedule.doctorTitle(),
                    schedule.workDate().toString(),
                    schedule.periodText(),
                    schedule.quotaUsed() + "/" + schedule.quotaTotal(),
                    schedule.statusText()));
        }
        ConsoleTable.print(SCHEDULE_HEADERS, rows);
    }

    /**
     * 查看锁定/失败账号并手动解锁。
     */
    private void manageLocks() {
        List<User> users = authService.lockedAccounts();
        List<List<String>> rows = new ArrayList<>();
        for (User item : users) {
            rows.add(List.of(
                    item.getUsername(),
                    item.getRealName(),
                    item.getRole().getLabel(),
                    String.valueOf(item.getFailedAttempts()),
                    item.isLocked() ? "锁定至 " + item.getLockedUntil().format(TIME) : "未锁定"));
        }
        if (rows.isEmpty()) {
            System.out.println("  当前没有被锁定或存在失败记录的账号。");
        } else {
            ConsoleTable.print(LOCK_HEADERS, rows);
            System.out.println("  规则：连续失败 " + authService.maxAttempts() + " 次锁定 "
                    + authService.lockMinutes() + " 分钟，到期自动解除。");
        }

        String username = InputUtils.readLine("  输入要解锁的登录名（0 取消）：");
        if ("0".equals(username)) {
            return;
        }
        authService.unlock(username);
        System.out.println("  ✓ 已解锁 " + username + "，失败次数已清零。");
    }
}