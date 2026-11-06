package service;

import entity.User;

public class UserService {
    /**
     * 登录校验
     * @param account 账号（对应user_info.account）
     * @param password 密码
     * @return 用户实体，失败返回null
     * 【数据库对接】
     *  SQL: select * from user_info where account = ?
     *  查询到用户后比对密码，返回User对象
     */
    public User login(String account, String password) {
        // =====数据库层实现代码=====
        // User user = userMapper.selectByAccount(account);
        // if(user != null && user.getPassword().equals(password)){
        //    return user;
        // }
        // return null;
        // =========================
        return null;
    }
}
