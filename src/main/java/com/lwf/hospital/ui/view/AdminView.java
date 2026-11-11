package com.lwf.hospital.ui.view;

import com.lwf.hospital.common.util.ConsoleTable;
import com.lwf.hospital.common.util.InputUtils;
import com.lwf.hospital.domain.dto.DoctorView;
import com.lwf.hospital.domain.dto.ScheduleView;
import com.lwf.hospital.domain.entity.Department;
import com.lwf.hospital.domain.entity.User;
import com.lwf.hospital.service.AuthService;
import com.lwf.hospital.service.DepartmentService;
import com.lwf.hospital.service.DoctorService;
import com.lwf.hospital.service.RegistrationService;
import com.lwf.hospital.service.ScheduleAdminService;
import com.lwf.hospital.ui.component.Menu;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员端：科室与医生的维护、号源总览、账号锁定管理。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class AdminView {

    private static final List<String> DEPT_HEADERS = List.of("ID", "科室编码", "科室名称", "位置");
    private static final List<String> DOCTOR_HEADERS = List.of(
            "ID", "工号", "姓名", "职称", "科室", "状态");
    private static final List<String> SCHEDULE_HEADERS = List.of(
            "ID", "科室", "医生", "职称", "日期", "时段", "已挂/总量", "剩余", "状态");
    private static final List<String> LOCK_HEADERS = List.of(
            "登录名", "姓名", "角色", "失败次数", "状态");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("MM-dd HH:mm");

    private final RegistrationService registrationService;
    private final AuthService authService;
    private final DepartmentService departmentService;
    private final DoctorService doctorService;
    private final ScheduleAdminService scheduleAdminService;
    private long currentUserId;

    public AdminView(RegistrationService registrationService,
                     AuthService authService,
                     DepartmentService departmentService,
                     DoctorService doctorService,
                     ScheduleAdminService scheduleAdminService) {
        this.registrationService = registrationService;
        this.authService = authService;
        this.departmentService = departmentService;
        this.doctorService = doctorService;
        this.scheduleAdminService = scheduleAdminService;
    }

    /**
     * 展示管理员菜单，直到返回。
     *
     * @param user 当前登录用户
     */
    public void show(User user) {
        this.currentUserId = user.getId();
        Menu menu = new Menu("管理员菜单 · " + user.getRealName());
        menu.add(1, "科室列表", this::printDepartments)
                .add(2, "号源总览", () -> printSchedules(registrationService.listAvailableSchedules(0)))
                .add(3, "科室管理（增删改）", this::manageDepartments)
                .add(4, "医生管理（增改停用）", this::manageDoctors)
                .add(5, "账号锁定与解锁", this::manageLocks)
                .add(6, "排班管理（排班/容量/停诊/加号）", this::manageSchedules)
                .add(7, "挂号统计报表（待实现）", () -> System.out.println("  该功能为后续扩展点，见 docs/01-项目说明.md"));
        menu.run();
    }

    // ---------------------------------------------------------------- 科室

    private void printDepartments() {
        List<Department> departments = departmentService.listAll();
        List<List<String>> rows = new ArrayList<>();
        for (Department department : departments) {
            rows.add(List.of(String.valueOf(department.getId()), department.getDeptCode(),
                    department.getName(), department.getLocation()));
        }
        ConsoleTable.print(DEPT_HEADERS, rows);
    }

    private void manageDepartments() {
        Menu menu = new Menu("科室管理");
        menu.add(1, "新增科室", this::createDepartment)
                .add(2, "修改科室", this::updateDepartment)
                .add(3, "删除科室", this::deleteDepartment);
        menu.run();
    }

    private void createDepartment() {
        String code = InputUtils.readNonBlank("  科室编码（如 NEI）：");
        String name = InputUtils.readNonBlank("  科室名称（如 内科）：");
        String location = InputUtils.readLine("  位置（可空）：");
        if (!InputUtils.confirm("  确认新增科室「" + code + " " + name + "」？")) {
            System.out.println("  已取消。");
            return;
        }
        long id = departmentService.create(code, name, location);
        System.out.println("  ✓ 新增成功，科室 id = " + id);
    }

    private void updateDepartment() {
        printDepartments();
        long id = InputUtils.readInt("  选择要修改的科室 ID（0 取消）：", 0, Integer.MAX_VALUE);
        if (id == 0) {
            return;
        }
        Department current = departmentService.listAll().stream()
                .filter(item -> item.getId() == id)
                .findFirst()
                .orElseThrow(() -> new com.lwf.hospital.common.exception.BizException("科室不存在（id=" + id + "）。"));
        String code = readOrKeep("  科室编码", current.getDeptCode());
        String name = readOrKeep("  科室名称", current.getName());
        String location = readOrKeep("  位置", current.getLocation());
        departmentService.update(id, code, name, location);
        System.out.println("  ✓ 已修改科室 " + id);
    }

    private void deleteDepartment() {
        printDepartments();
        long id = InputUtils.readInt("  选择要删除的科室 ID（0 取消）：", 0, Integer.MAX_VALUE);
        if (id == 0) {
            return;
        }
        if (!InputUtils.confirm("  删除后不可恢复，确认删除科室 " + id + "？")) {
            System.out.println("  已取消。");
            return;
        }
        departmentService.delete(id);
        System.out.println("  ✓ 已删除科室 " + id);
    }

    // ---------------------------------------------------------------- 医生

    private void manageDoctors() {
        Menu menu = new Menu("医生管理");
        menu.add(1, "医生列表", () -> printDoctors(doctorService.listAll()))
                .add(2, "新增医生", this::createDoctor)
                .add(3, "修改医生", this::updateDoctor)
                .add(4, "停用医生", this::disableDoctor)
                .add(5, "恢复医生", this::enableDoctor);
        menu.run();
    }

    private void printDoctors(List<DoctorView> doctors) {
        List<List<String>> rows = new ArrayList<>();
        for (DoctorView doctor : doctors) {
            rows.add(List.of(
                    String.valueOf(doctor.id()),
                    doctor.doctorNo(),
                    doctor.name(),
                    doctor.title(),
                    doctor.deptName(),
                    doctor.statusText()));
        }
        ConsoleTable.print(DOCTOR_HEADERS, rows);
    }

    private void createDoctor() {
        printDepartments();
        long deptId = InputUtils.readInt("  所属科室 ID（0 取消）：", 0, Integer.MAX_VALUE);
        if (deptId == 0) {
            return;
        }
        String no = InputUtils.readNonBlank("  工号（如 d1005）：");
        String name = InputUtils.readNonBlank("  姓名：");
        String title = InputUtils.readNonBlank("  职称（主任医师/副主任医师/主治医师/医师）：");
        if (!InputUtils.confirm("  确认新增医生「" + no + " " + name + " " + title + "」？")) {
            System.out.println("  已取消。");
            return;
        }
        long id = doctorService.create(no, name, title, deptId);
        System.out.println("  ✓ 新增成功，医生 id = " + id);
    }

    private void updateDoctor() {
        List<DoctorView> doctors = doctorService.listAll();
        printDoctors(doctors);
        long id = InputUtils.readInt("  选择要修改的医生 ID（0 取消）：", 0, Integer.MAX_VALUE);
        if (id == 0) {
            return;
        }
        DoctorView current = doctors.stream()
                .filter(item -> item.id() == id)
                .findFirst()
                .orElseThrow(() -> new com.lwf.hospital.common.exception.BizException("医生不存在（id=" + id + "）。"));
        String no = readOrKeep("  工号", current.doctorNo());
        String name = readOrKeep("  姓名", current.name());
        String title = readOrKeep("  职称", current.title());
        int deptId = InputUtils.readInt("  科室 ID（回车/0 保持不变，当前 " + current.deptName() + "）：",
                0, Integer.MAX_VALUE);
        long departmentId = deptId == 0 ? departmentIdOf(current) : deptId;
        doctorService.update(id, no, name, title, departmentId);
        System.out.println("  ✓ 已修改医生 " + id);
    }

    private long departmentIdOf(DoctorView doctor) {
        return departmentService.listAll().stream()
                .filter(dept -> dept.getName().equals(doctor.deptName()))
                .map(Department::getId)
                .findFirst()
                .orElseThrow(() -> new com.lwf.hospital.common.exception.BizException(
                        "无法识别医生所在科室：" + doctor.deptName()));
    }

    private void disableDoctor() {
        List<DoctorView> doctors = doctorService.listAll().stream()
                .filter(DoctorView::enabled)
                .toList();
        printDoctors(doctors);
        long id = InputUtils.readInt("  选择要停用的医生 ID（0 取消）：", 0, Integer.MAX_VALUE);
        if (id == 0) {
            return;
        }
        if (!InputUtils.confirm("  停用后该医生的未来号源会一并关闭，确认停用 " + id + "？")) {
            System.out.println("  已取消。");
            return;
        }
        int closed = doctorService.disable(id);
        System.out.println("  ✓ 已停用医生 " + id + "，同时关闭未来号源 " + closed + " 条。");
    }

    private void enableDoctor() {
        List<DoctorView> doctors = doctorService.listAll().stream()
                .filter(doctor -> !doctor.enabled())
                .toList();
        printDoctors(doctors);
        long id = InputUtils.readInt("  选择要恢复的医生 ID（0 取消）：", 0, Integer.MAX_VALUE);
        if (id == 0) {
            return;
        }
        doctorService.enable(id);
        System.out.println("  ✓ 已恢复医生 " + id + "。");
    }

    // ---------------------------------------------------------------- 号源 / 账号

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
                    String.valueOf(schedule.remaining()),
                    schedule.statusText()));
        }
        ConsoleTable.print(SCHEDULE_HEADERS, rows);
    }

    // ---------------------------------------------------------------- 排班

    private void manageSchedules() {
        Menu menu = new Menu("排班管理");
        menu.add(1, "排班总览（按日期区间）", this::viewSchedules)
                .add(2, "新增排班", this::createSchedule)
                .add(3, "修改号源容量", this::changeQuota)
                .add(4, "停诊", this::closeSchedule)
                .add(5, "恢复开诊", this::openSchedule)
                .add(6, "加号", this::addQuota);
        menu.run();
    }

    private void viewSchedules() {
        long deptId = InputUtils.readInt("  科室 ID（0 全部）：", 0, Integer.MAX_VALUE);
        LocalDate from = InputUtils.readDate("  起始日期", LocalDate.now());
        int days = InputUtils.readInt("  查看天数（1-30）：", 1, 30);
        printSchedules(scheduleAdminService.list(from, from.plusDays(days - 1L), deptId));
    }

    private void createSchedule() {
        List<DoctorView> doctors = doctorService.listAll().stream()
                .filter(DoctorView::enabled)
                .toList();
        printDoctors(doctors);
        long doctorId = InputUtils.readInt("  医生 ID（0 取消）：", 0, Integer.MAX_VALUE);
        if (doctorId == 0) {
            return;
        }
        LocalDate workDate = InputUtils.readDate("  出诊日期", LocalDate.now());
        String period = InputUtils.readLine("  时段（AM/PM 或 上午/下午）：");
        int quota = InputUtils.readInt("  号源总量（1-200）：", 1, 200);
        if (!InputUtils.confirm("  确认为该医生新增 " + workDate + " 的排班？")) {
            System.out.println("  已取消。");
            return;
        }
        long id = scheduleAdminService.create(doctorId, workDate, period, quota);
        System.out.println("  ✓ 新增排班成功，排班 id = " + id);
    }

    private void changeQuota() {
        printSchedules(upcoming());
        long id = InputUtils.readInt("  选择要修改的排班 ID（0 取消）：", 0, Integer.MAX_VALUE);
        if (id == 0) {
            return;
        }
        int quota = InputUtils.readInt("  新号源总量（1-200）：", 1, 200);
        scheduleAdminService.updateQuota(id, quota);
        System.out.println("  ✓ 已修改排班 " + id + " 的号源总量为 " + quota + "。");
    }

    private void closeSchedule() {
        List<ScheduleView> open = upcoming().stream()
                .filter(schedule -> "OPEN".equalsIgnoreCase(schedule.status()))
                .toList();
        printSchedules(open);
        long id = InputUtils.readInt("  选择要停诊的排班 ID（0 取消）：", 0, Integer.MAX_VALUE);
        if (id == 0) {
            return;
        }
        if (!InputUtils.confirm("  停诊后该时段将无法再挂号，确认停诊 " + id + "？")) {
            System.out.println("  已取消。");
            return;
        }
        int waiting = scheduleAdminService.close(id);
        System.out.println("  ✓ 已停诊排班 " + id + "。");
        if (waiting > 0) {
            System.out.println("  ⚠ 该排班下还有 " + waiting + " 位患者待就诊，请告知其改约或退号。");
        }
    }

    private void openSchedule() {
        List<ScheduleView> closed = upcoming().stream()
                .filter(schedule -> !"OPEN".equalsIgnoreCase(schedule.status()))
                .toList();
        printSchedules(closed);
        long id = InputUtils.readInt("  选择要恢复的排班 ID（0 取消）：", 0, Integer.MAX_VALUE);
        if (id == 0) {
            return;
        }
        scheduleAdminService.open(id);
        System.out.println("  ✓ 已恢复开诊排班 " + id + "。");
    }

    private void addQuota() {
        printSchedules(upcoming());
        long id = InputUtils.readInt("  选择要加号的排班 ID（0 取消）：", 0, Integer.MAX_VALUE);
        if (id == 0) {
            return;
        }
        int extra = InputUtils.readInt("  增加多少个号（1-50）：", 1, 50);
        scheduleAdminService.addQuota(id, extra, currentUserId);
        System.out.println("  ✓ 已加号 " + extra + " 个，并写入操作日志。");
    }

    /**
     * 今天起 7 天内的排班，供改量 / 停诊 / 加号选择。
     *
     * @return 排班视图
     */
    private List<ScheduleView> upcoming() {
        LocalDate today = LocalDate.now();
        return scheduleAdminService.list(today, today.plusDays(6), 0);
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

    /**
     * 读取输入；留空表示沿用原值。
     *
     * @param label   字段名
     * @param current 当前值
     * @return 新值或原值
     */
    private String readOrKeep(String label, String current) {
        String value = InputUtils.readLine("  " + label + "（回车保持「" + current + "」）：");
        return value.isBlank() ? current : value;
    }
}