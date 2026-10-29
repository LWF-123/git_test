package com.lwf.hospital.ui.view;

import com.lwf.hospital.common.util.ConsoleTable;
import com.lwf.hospital.domain.dto.ScheduleView;
import com.lwf.hospital.domain.entity.Department;
import com.lwf.hospital.domain.entity.User;
import com.lwf.hospital.service.RegistrationService;
import com.lwf.hospital.ui.component.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理员端：科室与号源总览（排班维护、统计报表为后续扩展点）。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class AdminView {

    private static final List<String> DEPT_HEADERS = List.of("ID", "科室编码", "科室名称", "位置");
    private static final List<String> SCHEDULE_HEADERS = List.of(
            "ID", "科室", "医生", "职称", "日期", "时段", "已挂/总量", "状态");

    private final RegistrationService registrationService;

    public AdminView(RegistrationService registrationService) {
        this.registrationService = registrationService;
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
                .add(4, "挂号统计报表（待实现）", () -> System.out.println("  该功能为后续扩展点，见 docs/01-项目说明.md"));
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
}