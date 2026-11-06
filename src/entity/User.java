package entity;

import java.util.Date;

public class User {
    public enum Role {
        ADMIN,      //管理员
        DOCTOR,     //医生
        PATIENT     //患者
    }

    private Integer userId;
    private String account;
    private String password;
    private String userName;
    private Date createTime;
    private Role role;

    public User(){}

    public User(Integer userId, String account, String password, String userName, Date createTime, Role role) {
        this.userId = userId;
        this.account = account;
        this.password = password;
        this.userName = userName;
        this.createTime = createTime;
        this.role = role;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
