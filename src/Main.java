import entity.User;
import service.UserService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserService userService = new UserService();

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
            case ADMIN -> adminMenu(scanner);
            case DOCTOR -> doctorMenu(scanner);
            case PATIENT -> patientMenu(scanner);
        }
    }

    //管理员菜单
    private static void adminMenu(Scanner sc){
        System.out.println("\n=====管理员菜单=====");
        System.out.println("1.科室与医生信息管理");
        System.out.println("2.排班号源管理");
        System.out.println("3.统计报表");
        System.out.println("0.退出");
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
