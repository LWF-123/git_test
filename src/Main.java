import entity.Dept;
import entity.Doctor;
import entity.Registration;
import entity.Schedule;
import entity.User;
import service.DeptService;
import service.RegistrationService;
import service.ScheduleService;
import service.UserService;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserService userService = new UserService();
        DeptService deptService = new DeptService();
        ScheduleService scheduleService = new ScheduleService();
        RegistrationService registerService = new RegistrationService();

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
            case PATIENT -> patientMenu(scanner, registerService, loginUser.getUserId());
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
    private static void patientMenu(Scanner sc, RegistrationService registerService, Integer userId){
        while(true){
            System.out.println("\n=====患者菜单=====");
            System.out.println("1.挂号");
            System.out.println("2.查询我的挂号");
            System.out.println("3.退号");
            System.out.println("0.退出");
            System.out.print("请选择：");
            int opt = sc.nextInt();
            switch(opt){
                case 1 -> registerPage(sc,registerService,userId);
                case 2 -> queryMyRegister(sc, registerService, userId);
                case 3 -> cancelRegister(sc, registerService, userId);
                case 0 -> {return;}
                default -> System.out.println("无效选项！");
            }
        }
    }

    //挂号页面逻辑：选科室 -> 选医生排班 -> 选号别 ->生成挂号
    private static void registerPage(Scanner sc, RegistrationService registerService, Integer userId){
        System.out.println("\n=====挂号流程=====");
        //1.选择科室
        List<Dept> deptList = registerService.getEnableDeptList();
        System.out.println("【科室列表】");
        //遍历打印科室
        System.out.print("请选择科室ID：");
        Integer deptId = sc.nextInt();

        //2.选择该科室在职医生
        List<Doctor> doctorList = registerService.getWorkDoctorByDeptId(deptId);
        System.out.println("【该科室医生列表】");
        //遍历打印医生
        System.out.print("请选择医生ID：");
        Integer docId = sc.nextInt();

        //3.查看该医生排班
        List<Schedule> scheduleList = registerService.getScheduleByDocId(docId);
        System.out.println("【医生排班列表】");
        System.out.print("选择就诊日期(yyyy-MM-dd)：");
        String dateStr = sc.next();
        Date visitDate = new Date();

        //4.选择号别
        System.out.println("请选择号别：1急诊 2专家 3普通");
        int typeOpt = sc.nextInt();
        Registration.RegType regType = switch (typeOpt){
            case 1 -> Registration.RegType.EMERGENCY;
            case 2 -> Registration.RegType.EXPERT;
            case 3 -> Registration.RegType.NORMAL;
            default -> null;
        };
        if(regType == null){
            System.out.println("号别选择错误！");
            return;
        }

        //5.录入房间号，组装挂号对象
        System.out.print("输入就诊房间号：");
        String roomNo = sc.next();
        Registration reg = new Registration();
        reg.setUserId(userId);
        reg.setDeptId(deptId);
        reg.setDocId(docId);
        reg.setRoomNo(roomNo);
        reg.setVisitDate(visitDate);
        reg.setRegStatus(Registration.RegStatus.WAIT.getCode());
        reg.setIsPay(0);
        reg.setRegType(regType);

        Registration resultReg = registerService.createRegister(reg);
        if(resultReg != null){
            System.out.println("✅挂号成功！排队号："+resultReg.getQueueNo());
        }else{
            System.out.println("❌挂号失败，号源已满或参数错误");
        }
    }

    //【功能5新增】查询我的挂号
    private static void queryMyRegister(Scanner sc, RegistrationService registerService, Integer userId){
        System.out.println("\n=====我的挂号记录=====");
        List<Registration> myRegList = registerService.getMyRegistration(userId);
        if(myRegList == null || myRegList.isEmpty()){
            System.out.println("暂无挂号记录");
            return;
        }
        for(Registration r : myRegList){
            System.out.printf("挂号ID:%d | 排队号:%s | 科室ID:%d | 医生ID:%d | 就诊日期:%s | 状态码:%d%n",
                    r.getRegId(), r.getQueueNo(), r.getDeptId(), r.getDocId(), r.getVisitDate(), r.getRegStatus());
        }
    }

    //【功能5新增】退号
    private static void cancelRegister(Scanner sc, RegistrationService registerService, Integer userId){
        System.out.println("\n=====退号操作=====");
        System.out.print("输入要取消的挂号ID：");
        Integer regId = sc.nextInt();
        boolean cancelResult = registerService.cancelRegistration(regId, userId);
        if(cancelResult){
            System.out.println("✅退号成功！挂号状态改为【已取消】");
        }else{
            System.out.println("❌退号失败！仅待就诊记录可以退号");
        }
    }
}
