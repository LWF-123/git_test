package service;

import entity.User;

public class UserService {
    /**
     * 登录校验
     * @param account 账号（user_info.account）
     * @param password 密码
     * @return 成功返回用户实体，失败返回null
     * 【数据库层实现】
     * SQL: select * from user_info where account = ?
     * 查询用户，比对密码后返回User对象
     */
    public User login(String account, String password) {
        // User user = userMapper.selectByAccount(account);
        // if(user != null && user.getPassword().equals(password)){
        //     return user;
        // }
        // return null;
        return null;
    }
}
