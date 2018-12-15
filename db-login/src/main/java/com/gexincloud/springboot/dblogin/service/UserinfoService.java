package com.gexincloud.springboot.dblogin.service;

import com.gexincloud.springboot.dblogin.entity.Userinfo;
import com.github.pagehelper.PageInfo;

public interface UserinfoService {
    //根据阿里巴巴的开发规范
    //查找单条数据的业务以find开头
    //查找多条数据以list开头
    //删除以delete开头
    //新增以add或者insert开头
    //修改以update开头
    //实现一个根据用户名查找的功能
    Userinfo findByUsername(String username);

    Userinfo login(String username,String password);

    //分页查询
    PageInfo<Userinfo> page(int pageNo,int pageSize,Userinfo record);
}
