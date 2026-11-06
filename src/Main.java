import entity.Dept;
import entity.Doctor;
import entity.Schedule;
import entity.User;
import service.DeptService;
import service.ScheduleService;
import service.UserService;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserService userService = new UserService();
        DeptService deptService = new DeptService();
        ScheduleService scheduleService = new ScheduleService();

        System.out.println("===== 门诊挂号系统 =====");
        System.out.print("请输入账号：");
        String account = scanner.next();
        System.out.print("请输入密码：");
        String password = scanner.next();

        User loginUser = userService.login(account, password);
        if(loginUser == null){
            System.out.println("账号或密码错误，登录失败！");
            return;
        }

        System.out.println("\n✅登录成功！");
        System.out.println("账号：" + loginUser.getAccount());
        System.out.println("姓名：" + loginUser.getUserName());
        String roleName = switch (loginUser.getRole()){
            case ADMIN -> "管理员";
            case DOCTOR -> "医生";
            case PATIENT -> "患者";
        };
        System.out.println("角色：" + roleName);

        switch (loginUser.getRole()){
            case ADMIN -> adminMenu(scanner, deptService, scheduleService);
            case DOCTOR -> doctorMenu(scanner);
            case PATIENT -> patientMenu(scanner);
        }
    }

    //管理员菜单
    private static void adminMenu(Scanner sc, DeptService deptService, ScheduleService scheduleService){
        while(true) {
            System.out.println("\n=====管理员主菜单=====");
            System.out.println("1.科室与医生信息管理");
            System.out.println("2.排班号源管理");
            System.out.println("3.统计报表");
            System.out.println("0.退出");
            System.out.print("请选择：");
            int opt = sc.nextInt();
            switch (opt) {
                case 1 -> deptDoctorMenu(sc, deptService);
                case 2 -> scheduleMenu(sc, scheduleService);
                case 3 -> System.out.println("【统计报表模块待开发】");
                case 0 -> {
                    System.out.println("退出系统");
                    return;
                }
                default -> System.out.println("输入选项无效！");
            }
        }
    }

    //排班管理子菜单
    private static void scheduleMenu(Scanner sc, ScheduleService scheduleService){
        while(true){
            System.out.println("\n=====排班（号源）管理=====");
            System.out.println("1.新增排班");
            System.out.println("2.修改排班信息");
            System.out.println("3.修改号源容量（加号）");
            System.out.println("4.停诊（指定星期休息）");
            System.out.println("5.查询全部排班");
            System.out.println("6.根据医生ID查询排班");
            System.out.println("0.返回上一级");
            System.out.print("请选择：");
            int opt = sc.nextInt();
            switch(opt){
                case 1 -> {
                    System.out.print("医生ID：");
                    int docId = sc.nextInt();
                    System.out.print("周一(0休息/1出诊):");
                    int mon = sc.nextInt();
                    System.out.print("周二(0休息/1出诊):");
                    int tue = sc.nextInt();
                    System.out.print("周三(0休息/1出诊):");
                    int wed = sc.nextInt();
                    System.out.print("周四(0休息/1出诊):");
                    int thu = sc.nextInt();
                    System.out.print("周五(0休息/1出诊):");
                    int fri = sc.nextInt();
                    System.out.print("周六(0休息/1出诊):");
                    int sat = sc.nextInt();
                    System.out.print("周日(0休息/1出诊):");
                    int sun = sc.nextInt();
                    System.out.print("当日最大号源：");
                    int max = sc.nextInt();
                    Schedule schedule = new Schedule(null,docId,mon,tue,wed,thu,fri,sat,sun,max);
                    boolean res = scheduleService.addSchedule(schedule);
                    System.out.println(res?"新增排班成功":"新增失败");
                }
                case 2 -> {
                    System.out.print("排班ID：");
                    int sid = sc.nextInt();
                    System.out.print("医生ID：");
                    int docId = sc.nextInt();
                    System.out.print("周一(0休息/1出诊):");
                    int mon = sc.nextInt();
                    System.out.print("周二(0休息/1出诊):");
                    int tue = sc.nextInt();
                    System.out.print("周三(0休息/1出诊):");
                    int wed = sc.nextInt();
                    System.out.print("周四(0休息/1出诊):");
                    int thu = sc.nextInt();
                    System.out.print("周五(0休息/1出诊):");
                    int fri = sc.nextInt();
                    System.out.print("周六(0休息/1出诊):");
                    int sat = sc.nextInt();
                    System.out.print("周日(0休息/1出诊):");
                    int sun = sc.nextInt();
                    System.out.print("最大号源：");
                    int max = sc.nextInt();
                    Schedule schedule = new Schedule(sid,docId,mon,tue,wed,thu,fri,sat,sun,max);
                    boolean res = scheduleService.updateSchedule(schedule);
                    System.out.println(res?"修改排班成功":"修改失败");
                }
                case 3 -> {
                    System.out.print("排班ID：");
                    int sid = sc.nextInt();
                    System.out.print("输入新的最大号源容量：");
                    int newMax = sc.nextInt();
                    boolean res = scheduleService.changeMaxNum(sid, newMax);
                    System.out.println(res?"加号成功":"加号失败");
                }
                case 4 -> {
                    System.out.print("排班ID：");
                    int sid = sc.nextInt();
                    System.out.print("选择星期：1周一 2周二 3周三 4周四 5周五 6周六 7周日：");
                    int week = sc.nextInt();
                    boolean res = scheduleService.stopDiagnosis(sid, week);
                    System.out.println(res?"停诊设置成功":"停诊失败");
                }
                case 5 -> {
                    List<Schedule> list = scheduleService.getAllSchedule();
                    System.out.println("====全部排班列表====");
                }
                case 6 -> {
                    System.out.print("医生ID：");
                    int docId = sc.nextInt();
                    List<Schedule> list = scheduleService.getScheduleByDocId(docId);
                    System.out.println("====该医生排班====");
                }
                case 0 -> {return;}
                default -> System.out.println("无效选项！");
            }
        }
    }

    //科室和医生管理子菜单
    private static void deptDoctorMenu(Scanner sc, DeptService deptService){
        while(true){
            System.out.println("\n=====科室与医生管理=====");
            System.out.println("1.新增科室");
            System.out.println("2.修改科室");
            System.out.println("3.查看全部科室");
            System.out.println("4.启用/停用科室");
            System.out.println("5.新增医生");
            System.out.println("6.修改医生信息");
            System.out.println("7.查看全部医生");
            System.out.println("8.按科室查询医生");
            System.out.println("9.修改医生在职状态");
            System.out.println("0.返回上一级");
            System.out.print("请选择：");
            int opt = sc.nextInt();
            switch(opt){
                case 1 -> {
                    System.out.print("输入科室名称：");
                    String name = sc.next();
                    System.out.print("输入房间ID：");
                    int roomId = sc.nextInt();
                    Dept dept = new Dept(null,name,roomId,1);
                    boolean res = deptService.addDept(dept);
                    System.out.println(res?"新增成功":"新增失败");
                }
                case 2 -> {
                    System.out.print("输入要修改的科室ID：");
                    int id = sc.nextInt();
                    System.out.print("输入新科室名：");
                    String name = sc.next();
                    System.out.print("输入新房间ID：");
                    int roomId = sc.nextInt();
                    Dept dept = new Dept(id,name,roomId,1);
                    boolean res = deptService.updateDept(dept);
                    System.out.println(res?"修改成功":"修改失败");
                }
                case 3 -> {
                    List<Dept> list = deptService.getAllDept();
                    System.out.println("====科室列表====");
                }
                case 4 -> {
                    System.out.print("科室ID：");
                    int deptId = sc.nextInt();
                    System.out.print("1启用 0停用，请输入状态：");
                    int status = sc.nextInt();
                    boolean res = deptService.changeDeptStatus(deptId,status);
                    System.out.println(res?"状态修改成功":"失败");
                }
                case 5 -> {
                    System.out.print("医生姓名：");
                    String docName = sc.next();
                    System.out.print("医生等级(专家/普通)：");
                    String level = sc.next();
                    System.out.print("所属科室ID：");
                    int deptId = sc.nextInt();
                    Doctor doctor = new Doctor(null,docName,level,deptId,1);
                    boolean res = deptService.addDoctor(doctor);
                    System.out.println(res?"新增医生成功":"失败");
                }
                case 6 -> {
                    System.out.print("待修改医生ID：");
                    int docId = sc.nextInt();
                    System.out.print("新姓名：");
                    String name = sc.next();
                    System.out.print("新等级：");
                    String level = sc.next();
                    System.out.print("所属科室ID：");
                    int deptId = sc.nextInt();
                    Doctor doctor = new Doctor(docId,name,level,deptId,1);
                    boolean res = deptService.updateDoctor(doctor);
                    System.out.println(res?"修改成功":"失败");
                }
                case 7 -> {
                    List<Doctor> list = deptService.getAllDoctor();
                    System.out.println("====医生列表====");
                }
                case 8 -> {
                    System.out.print("输入科室ID：");
                    int deptId = sc.nextInt();
                    List<Doctor> list = deptService.getDoctorByDeptId(deptId);
                    System.out.println("====该科室医生列表====");
                }
                case 9 -> {
                    System.out.print("医生ID：");
                    int docId = sc.nextInt();
                    System.out.print("1在职 0离职，请输入状态：");
                    int workStatus = sc.nextInt();
                    boolean res = deptService.changeDoctorWorkStatus(docId,workStatus);
                    System.out.println(res?"状态修改成功":"失败");
                }
                case 0 -> {return;}
                default -> System.out.println("无效选项！");
            }
        }
    }

    //医生菜单
    private static void doctorMenu(Scanner sc){
        System.out.println("\n=====医生菜单=====");
        System.out.println("1.候诊队列与叫号");
        System.out.println("2.接诊完成/过号");
        System.out.println("0.退出");
    }

    //患者菜单
    private static void patientMenu(Scanner sc){
        System.out.println("\n=====患者菜单=====");
        System.out.println("1.挂号");
        System.out.println("2.查询我的挂号");
        System.out.println("3.退号");
        System.out.println("0.退出");
    }
}
