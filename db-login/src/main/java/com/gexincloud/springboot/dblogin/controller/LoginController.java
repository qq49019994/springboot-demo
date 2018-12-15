package com.gexincloud.springboot.dblogin.controller;

import com.gexincloud.springboot.dblogin.entity.Userinfo;
import com.gexincloud.springboot.dblogin.service.UserinfoService;
import com.gexincloud.springboot.dblogin.util.CookieUtils;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private UserinfoService userinfoService;
    @GetMapping(value = "/")
    public String index(HttpServletRequest request,HttpServletResponse response){
        //跳转前就要判断Cookie中是否有token，如果有直接跳转主页
        String token = CookieUtils.getCookieValue(request,"token");
        if(!StringUtils.isEmpty(token)){
            //现在如果有记住我功能就要去数据库中找是哪个用户在这里记住登录了
            Userinfo userinfo = userinfoService.findByUsername(token);
            if(userinfo!=null){
                request.getSession().setAttribute("userinfo",userinfo);
                return "redirect:/main";
            }else{
                //如果从Cookie中获取的账号不存在，说明Cookie已经失效了，删掉这个Cookie
                CookieUtils.deleteCookie(request,response,"token");
            }
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
        //根据阿里的开发规范 .equals方法必须把字符串放前，变量放后，防止NNP的出现
        //调用service层的login方法
        Userinfo userinfo = userinfoService.login(username,password);
        if(userinfo!=null){
            //登录成功后把值放入session中
            request.getSession().setAttribute("userinfo",userinfo);
            //登录成功，跳转主页
            //跳转前我们先判断用户是不是勾选了记住我
            if("1".equals(rememberMe)){
                //Cookie以毫秒计数
                CookieUtils.setCookie(request,response,"token",userinfo.getUsername(),1000*60*60*24*15);
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

    //一个简单的根据账号查找实现，现在我们写登录业务
    @ResponseBody
    @GetMapping(value = "/getByUsername")
    public Userinfo getByUsername(String username){
        return userinfoService.findByUsername(username);
    }
}
