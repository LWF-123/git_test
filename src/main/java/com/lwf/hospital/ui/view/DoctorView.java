package com.lwf.hospital.ui.view;

import com.lwf.hospital.common.exception.BizException;
import com.lwf.hospital.common.util.ConsoleTable;
import com.lwf.hospital.common.util.InputUtils;
import com.lwf.hospital.dao.ScheduleDao;
import com.lwf.hospital.domain.dto.RegistrationView;
import com.lwf.hospital.domain.dto.ScheduleView;
import com.lwf.hospital.domain.entity.User;
import com.lwf.hospital.service.QueueService;
import com.lwf.hospital.ui.component.Menu;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 医生端：选择今日排班后查看候诊队列、叫号、过号、完成接诊。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class DoctorView {

    private static final List<String> SCHEDULE_HEADERS = List.of(
            "ID", "科室", "日期", "时段", "已挂/总量", "状态");
    private static final List<String> QUEUE_HEADERS = List.of(
            "挂号单", "患者", "类型", "排队号", "状态", "费用");

    private final QueueService queueService;
    private final ScheduleDao scheduleDao;

    public DoctorView(QueueService queueService, ScheduleDao scheduleDao) {
        this.queueService = queueService;
        this.scheduleDao = scheduleDao;
    }

    /**
     * 展示医生菜单，直到返回。
     *
     * @param user 当前登录用户
     */
    public void show(User user) {
        if (user.getRefId() == null) {
            throw new BizException("该账号未绑定医生信息，请联系管理员。");
        }
        long doctorId = user.getRefId();
        List<ScheduleView> todaySchedules = scheduleDao.findByDoctorAndDate(doctorId, LocalDate.now());
        if (todaySchedules.isEmpty()) {
            System.out.println("  你今天没有排班。");
            return;
        }

        long scheduleId = pickSchedule(todaySchedules);
        if (scheduleId == 0) {
            return;
        }
        ScheduleView current = todaySchedules.stream()
                .filter(item -> item.scheduleId() == scheduleId)
                .findFirst()
                .orElseThrow();

        Menu menu = new Menu("医生菜单 · " + user.getRealName() + " · "
                + current.deptName() + " " + current.workDate() + " " + current.periodText());
        menu.add(1, "查看候诊队列", () -> printQueue(scheduleId))
                .add(2, "叫下一位", () -> callNext(scheduleId))
                .add(3, "过    号", () -> markQueue(scheduleId, true))
                .add(4, "完成接诊", () -> markQueue(scheduleId, false));
        menu.run();
    }

    private long pickSchedule(List<ScheduleView> schedules) {
        if (schedules.size() == 1) {
            return schedules.get(0).scheduleId();
        }
        List<List<String>> rows = new ArrayList<>();
        for (ScheduleView schedule : schedules) {
            rows.add(List.of(
                    String.valueOf(schedule.scheduleId()),
                    schedule.deptName(),
                    schedule.workDate().toString(),
                    schedule.periodText(),
                    schedule.quotaUsed() + "/" + schedule.quotaTotal(),
                    schedule.statusText()));
        }
        ConsoleTable.print(SCHEDULE_HEADERS, rows);
        return InputUtils.readInt("  选择本次接诊的排班 ID（0 返回）：", 0, Integer.MAX_VALUE);
    }

    private void printQueue(long scheduleId) {
        List<RegistrationView> queue = queueService.waitingQueue(scheduleId);
        List<List<String>> rows = new ArrayList<>();
        for (RegistrationView item : queue) {
            rows.add(List.of(
                    String.valueOf(item.registrationId()),
                    item.patientName(),
                    item.type().getLabel(),
                    String.valueOf(item.queueNo()),
                    item.status().getLabel(),
                    String.valueOf(item.fee())));
        }
        ConsoleTable.print(QUEUE_HEADERS, rows);
    }

    private void callNext(long scheduleId) {
        RegistrationView next = queueService.callNext(scheduleId);
        if (next == null) {
            System.out.println("  当前没有候诊患者。");
            return;
        }
        System.out.println("  ✓ 请 " + next.queueNo() + " 号 " + next.patientName()
                + "（" + next.type().getLabel() + "）到诊室就诊。");
    }

    private void markQueue(long scheduleId, boolean skip) {
        printQueue(scheduleId);
        long registrationId = InputUtils.readInt("  输入挂号单 ID（0 取消）：", 0, Integer.MAX_VALUE);
        if (registrationId == 0) {
            return;
        }
        if (skip) {
            queueService.skip(registrationId);
            System.out.println("  ✓ 已标记 " + registrationId + " 号为过号。");
        } else {
            queueService.complete(registrationId);
            System.out.println("  ✓ 已完成 " + registrationId + " 号的接诊。");
        }
    }
}