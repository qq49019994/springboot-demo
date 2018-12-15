package com.gexincloud.springboot.shiro.service;

import com.gexincloud.springboot.shiro.entity.SysUser;

import java.sql.SQLException;
import java.util.List;

public interface SysUserService {

    SysUser login(String username);

    List<SysUser> list(SysUser record);

    //根据58到家的数据库规范，增删改操作的业务必须抛出SQL异常
    int insert(SysUser sysUser) throws SQLException;
}
