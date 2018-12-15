package com.gexincloud.springboot.dblogin.controller;

import com.gexincloud.springboot.dblogin.entity.Userinfo;
import com.gexincloud.springboot.dblogin.service.UserinfoService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/userinfo")
public class UserinfoController {
    @Autowired
    private UserinfoService userinfoService;
    //使用路径参数，使我们的请求更像restful风格
    @ResponseBody
    @GetMapping(value = "/page/{pageNo}/{pageSize}")
    public PageInfo<Userinfo> pageInfo(@PathVariable int pageNo,@PathVariable int pageSize,Userinfo record){
        return userinfoService.page(pageNo,pageSize,record);
    }
}
