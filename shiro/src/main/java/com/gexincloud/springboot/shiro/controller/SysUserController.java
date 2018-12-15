package com.gexincloud.springboot.shiro.controller;

import com.gexincloud.springboot.shiro.entity.SysUser;
import com.gexincloud.springboot.shiro.service.SysUserService;
import com.gexincloud.springboot.shiro.util.ShiroMd5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;

@Controller
@RequestMapping(value = "/sysUser")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    //返回结果为空时，则表示数据库内这个记录不重复
    @ResponseBody
    @PostMapping(value = "/validate")
    public boolean validate(SysUser record){
        return sysUserService.list(record).isEmpty();
    }

    @ResponseBody
    @PostMapping(value = "/insert")
    public boolean insert(SysUser sysUser){
        //注册方法
        //前面教程我们提到过不能把所有验证都交给前端去验证，因为有人会可能绕过这个验证，所以这里我们做二次验证
        SysUser record = new SysUser();
        record.setUsername(sysUser.getUsername());
        if(sysUserService.list(record).isEmpty()){
            sysUser.setPassword(ShiroMd5Util.getMd5Str(sysUser.getPassword(),sysUser.getUsername()));
            //插入方法会返回int的值，代表XX行数据被更改
            //0行数据被更改肯定是插入失败了，虽然没sql错误
            try {
                return sysUserService.insert(sysUser)>0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
