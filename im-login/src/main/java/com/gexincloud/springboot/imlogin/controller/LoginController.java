package com.gexincloud.springboot.imlogin.controller;

import com.gexincloud.springboot.imlogin.util.CookieUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @GetMapping(value = "/")
    public String index(HttpServletRequest request){
        //跳转前就要判断Cookie中是否有token且token为admin，如果有直接跳转主页
        String token = CookieUtils.getCookieValue(request,"token");
        if(!StringUtils.isEmpty(token)&&"admin".equals(token)){
            return "redirect:/main";
        }
        return "/login";
    }

    @ResponseBody
    @PostMapping(value = "/")
    public String index2(){
        return "你使用了POST请求";
    }

    //Http请求 分GET POST PUT OPTIONS DELETE 在不指定时可以使用任何方式请求次方法，很明显这不科学
    //同一个请求地址可以根据 请求的header不同做出不同的响应
    //下面演示一下入口在get和post时有什么不一样
    //我们使用postman工具
    //由于前台form使用post提交，我们这里也写一个POST
    @PostMapping(value = "/login")
    public String login(HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes, String username, String password, String rememberMe){
        //现在我们模拟 账号=admin 密码=123456
        //根据阿里的开发规范 .equals方法必须把字符串放前，变量放后，防止NNP的出现
        if("admin".equals(username)&&"123456".equals(password)){
            //登录成功，跳转主页
            //跳转前我们先判断用户是不是勾选了记住我
            if("1".equals(rememberMe)){
                //Cookie以毫秒计数
                CookieUtils.setCookie(request,response,"token","admin",1000*60*60*24*15);
            }
            //转发到主页，防止出现多次请求问题
            return "redirect:/main";
        }else{
            //登录失败，返回错误信息并跳回登录页
            //转发回原页面，防止出现多次请求的问题
            redirectAttributes.addAttribute("error","账号或密码错误");
            return "redirect:/";
        }
    }

    @GetMapping(value = "/main")
    public String main(){
        return "/main";
    }
}
