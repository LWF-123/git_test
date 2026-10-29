package com.lwf.hospital.config;

import com.lwf.hospital.common.exception.DataAccessException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库配置：按“环境变量 &gt; classpath:db.properties &gt; classpath:db.properties.example”的顺序加载。
 *
 * <p>仓库只提交 {@code db.properties.example}，每人复制一份 {@code db.properties} 填本地账号，
 * 后者已被 .gitignore 排除，避免把密码推上 GitHub。</p>
 *
 * @author LWF-123
 * @since 1.0.0
 */
public final class DbConfig {

    private static final String EXAMPLE_RESOURCE = "/db.properties.example";
    private static final String LOCAL_RESOURCE = "/db.properties";

    private final String driver;
    private final String url;
    private final String user;
    private final String password;
    private boolean driverLoaded;

    private DbConfig(String driver, String url, String user, String password) {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * 加载配置。
     *
     * @return 配置实例
     */
    public static DbConfig load() {
        Properties props = new Properties();
        loadResource(props, EXAMPLE_RESOURCE);
        loadResource(props, LOCAL_RESOURCE);

        String driver = firstNotBlank(System.getenv("HQ_DB_DRIVER"), props.getProperty("db.driver"),
                "com.mysql.cj.jdbc.Driver");
        String url = firstNotBlank(System.getenv("HQ_DB_URL"), props.getProperty("db.url"), "");
        String user = firstNotBlank(System.getenv("HQ_DB_USER"), props.getProperty("db.user"), "root");
        String password = firstNotBlank(System.getenv("HQ_DB_PASSWORD"), props.getProperty("db.password"), "");

        if (url.isBlank()) {
            throw new DataAccessException("未找到数据库地址：请复制 src/main/resources/db.properties.example 为 db.properties 并填写 db.url");
        }
        return new DbConfig(driver, url, user, password);
    }

    private static void loadResource(Properties props, String resource) {
        try (InputStream in = DbConfig.class.getResourceAsStream(resource)) {
            if (in == null) {
                return;
            }
            props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new DataAccessException("读取数据库配置失败：" + resource, e);
        }
    }

    private static String firstNotBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }

    /**
     * 打开一个新连接，调用方负责关闭。
     *
     * @return 数据库连接
     */
    public synchronized Connection open() {
        ensureDriverLoaded();
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new DataAccessException("连接数据库失败：" + e.getMessage(), e);
        }
    }

    private void ensureDriverLoaded() {
        if (driverLoaded) {
            return;
        }
        try {
            Class.forName(driver);
            driverLoaded = true;
        } catch (ClassNotFoundException e) {
            throw new DataAccessException("缺少 JDBC 驱动 " + driver + "，请确认依赖 mysql-connector-j 已下载", e);
        }
    }

    /**
     * 连接串（启动时打印用）。
     *
     * @return 连接串
     */
    public String getUrl() {
        return url;
    }

    /**
     * 用户名。
     *
     * @return 用户名
     */
    public String getUser() {
        return user;
    }
}