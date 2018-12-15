package com.gexincloud.springboot.shiro.controller;

import com.gexincloud.springboot.shiro.util.ShiroMd5Util;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    //这里我们需要判断，是否已经登录，如果登录过则直接进入main
    @GetMapping(value = "/")
    public String login(HttpServletRequest request){
        //shiro中我们使用subject对象获取登录信息
        Subject currentUser = SecurityUtils.getSubject();
        if(currentUser.isAuthenticated()){
            return "redirect:main";
        }
        return "/login";
    }

    @GetMapping(value = "/main")
    public String main(){
        return "main";
    }

    //登录方法
    @PostMapping(value = "/login")
    public String login(String username,String password){
        //这里我们测试一下密码加密策略
        Subject currentUser = SecurityUtils.getSubject();
        if(currentUser.isAuthenticated()){
            return "redirect:/main";
        }
        //使用AuthenticationToken登录
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
        currentUser.login(usernamePasswordToken);
        return "redirect:/main";
    }

    //注册页面
    @GetMapping(value = "/regist")
    public String regist(){
        return "/regist";
    }
}