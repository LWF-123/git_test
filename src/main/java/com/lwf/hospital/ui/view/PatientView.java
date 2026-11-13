package com.lwf.hospital.ui.view;

import com.lwf.hospital.common.exception.BizException;
import com.lwf.hospital.common.util.ConsoleTable;
import com.lwf.hospital.common.util.InputUtils;
import com.lwf.hospital.domain.dto.RegistrationView;
import com.lwf.hospital.domain.dto.ScheduleView;
import com.lwf.hospital.domain.entity.Department;
import com.lwf.hospital.domain.entity.User;
import com.lwf.hospital.domain.enums.RegistrationType;
import com.lwf.hospital.domain.enums.TicketStatus;
import com.lwf.hospital.service.RegistrationService;
import com.lwf.hospital.ui.component.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * 患者端：查科室、查号源、挂号、查我的挂号。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class PatientView {

    private static final List<String> DEPT_HEADERS = List.of("ID", "科室编码", "科室名称", "位置");
    private static final List<String> SCHEDULE_HEADERS = List.of(
            "ID", "科室", "医生", "职称", "日期", "时段", "已挂/总量", "状态");
    private static final List<String> MY_HEADERS = List.of(
            "挂号单", "科室", "医生", "日期", "时段", "类型", "排队号", "状态", "费用");

    private final RegistrationService registrationService;

    public PatientView(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    /**
     * 展示患者菜单，直到返回。
     *
     * @param user 当前登录用户
     */
    public void show(User user) {
        long patientId = requirePatientId(user);
        Menu menu = new Menu("患者菜单 · " + user.getRealName());
        menu.add(1, "科室列表", this::printDepartments)
                .add(2, "查询号源", this::printAllSchedules)
                .add(3, "挂    号", () -> register(patientId))
                .add(4, "我的挂号（可筛选）", () -> printMyRegistrations(patientId))
                .add(5, "退    号", () -> cancel(patientId));
        menu.run();
    }

    private long requirePatientId(User user) {
        if (user.getRefId() == null) {
            throw new BizException("该账号未绑定患者信息，请联系管理员。");
        }
        return user.getRefId();
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

    private void printAllSchedules() {
        printSchedules(registrationService.listAvailableSchedules(0));
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

    private void register(long patientId) {
        printDepartments();
        long departmentId = InputUtils.readInt("  选择科室 ID（0 取消）：", 0, Integer.MAX_VALUE);
        if (departmentId == 0) {
            return;
        }
        List<ScheduleView> schedules = registrationService.listAvailableSchedules(departmentId);
        if (schedules.isEmpty()) {
            System.out.println("  该科室暂无可挂号源。");
            return;
        }
        printSchedules(schedules);
        long scheduleId = InputUtils.readInt("  选择排班 ID（0 取消）：", 0, Integer.MAX_VALUE);
        if (scheduleId == 0) {
            return;
        }
        ScheduleView target = schedules.stream()
                .filter(item -> item.scheduleId() == scheduleId)
                .findFirst()
                .orElseThrow(() -> new BizException("列表中不存在 ID 为 " + scheduleId + " 的排班。"));

        System.out.println("  1. 普通号（10 元）  2. 专家号（30 元）  3. 急诊号（50 元，优先叫号）");
        int typeChoice = InputUtils.readInt("  选择挂号类型（0 取消）：", 0, 3);
        if (typeChoice == 0) {
            return;
        }
        RegistrationType type = switch (typeChoice) {
            case 1 -> RegistrationType.NORMAL;
            case 2 -> RegistrationType.EXPERT;
            default -> RegistrationType.EMERGENCY;
        };

        if (!InputUtils.confirm("  确认挂「" + target.deptName() + " / " + target.doctorName()
                + " / " + target.workDate() + " " + target.periodText() + "」的" + type.getLabel() + "？")) {
            System.out.println("  已取消。");
            return;
        }

        RegistrationView registration = registrationService.register(patientId, scheduleId, type);
        System.out.println("  ✓ 挂号成功：排队号 " + registration.queueNo()
                + "，类型 " + registration.type().getLabel()
                + "，挂号费 " + registration.fee() + " 元");
        System.out.println("    请到 " + target.deptName() + " 候诊区等待叫号。");
    }

    private void printMyRegistrations(long patientId) {
        TicketStatus filter = askStatusFilter();
        List<RegistrationView> registrations = registrationService.myRegistrations(patientId, filter);
        if (registrations.isEmpty()) {
            System.out.println("  没有符合条件的挂号记录。");
            return;
        }
        printRegistrations(registrations);
    }

    /**
     * 退号：只允许退“候诊”状态的号，退后号源回补、排队号作废。
     *
     * @param patientId 患者 id
     */
    private void cancel(long patientId) {
        List<RegistrationView> waiting = registrationService.myRegistrations(patientId, TicketStatus.WAITING);
        if (waiting.isEmpty()) {
            System.out.println("  你没有可退的挂号（只有“候诊”状态可以退号）。");
            return;
        }
        printRegistrations(waiting);
        long registrationId = InputUtils.readInt("  输入要退的挂号单编号（0 取消）：", 0, Integer.MAX_VALUE);
        if (registrationId == 0) {
            return;
        }
        RegistrationView target = waiting.stream()
                .filter(item -> item.registrationId() == registrationId)
                .findFirst()
                .orElseThrow(() -> new BizException("列表中不存在编号为 " + registrationId + " 的可退挂号。"));

        if (!InputUtils.confirm("  确认退「" + target.deptName() + " / " + target.doctorName()
                + " / " + target.workDate() + " " + target.periodText() + "」的号？")) {
            System.out.println("  已取消。");
            return;
        }

        RegistrationView cancelled = registrationService.cancel(patientId, registrationId);
        System.out.println("  ✓ 退号成功：排队号 " + cancelled.queueNo() + " 已作废，号源已回补。");
        System.out.println("    挂号费 " + cancelled.fee() + " 元已原路退回（模拟）。");
    }

    private TicketStatus askStatusFilter() {
        System.out.println("  状态筛选：0 全部  1 候诊  2 叫号中  3 已完成  4 已过号  5 已取消");
        int choice = InputUtils.readInt("  选择状态（直接回车=0 全部）：", 0, 5);
        return switch (choice) {
            case 1 -> TicketStatus.WAITING;
            case 2 -> TicketStatus.CALLING;
            case 3 -> TicketStatus.SERVED;
            case 4 -> TicketStatus.SKIPPED;
            case 5 -> TicketStatus.CANCELLED;
            default -> null;
        };
    }

    private void printRegistrations(List<RegistrationView> registrations) {
        List<List<String>> rows = new ArrayList<>();
        for (RegistrationView item : registrations) {
            rows.add(List.of(
                    String.valueOf(item.registrationId()),
                    item.deptName(),
                    item.doctorName(),
                    item.workDate().toString(),
                    item.periodText(),
                    item.type().getLabel(),
                    String.valueOf(item.queueNo()),
                    item.status().getLabel(),
                    String.valueOf(item.fee())));
        }
        ConsoleTable.print(MY_HEADERS, rows);
    }
}