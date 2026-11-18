import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcDemo {
    public static void main(String[] args) {
        // 1. 连接信息
        String url = "jdbc:mysql://localhost:3306/testdb?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        String user = " ";
        String password = " ";

        Connection conn = null;
        try {
            // 2. 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 3. 获取连接
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("数据库连接成功！");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            // 4. 关闭连接
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
