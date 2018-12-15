package com.gexincloud.springboot.dblogin.service.impl;

import com.gexincloud.springboot.dblogin.entity.Userinfo;
import com.gexincloud.springboot.dblogin.mapper.UserinfoMapper;
import com.gexincloud.springboot.dblogin.service.UserinfoService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserinfoServiceImpl implements UserinfoService {

    @Autowired
    private UserinfoMapper userinfoMapper;

    //实现接口
    @Override
    public Userinfo findByUsername(String username) {
        //这里我们从数据库设计上已知肯定是返回单条数据，所有使用selectOne
        //如果username传入空值，我们为了防止出现NNP，做一下判断
        if(StringUtils.isEmpty(username)){
            return null;
        }
        Userinfo record = new Userinfo();
        record.setUsername(username);
        return  userinfoMapper.selectOne(record);
    }

    @Override
    public Userinfo login(String username, String password) {
        //由于我们前面写了根据用户名查找功能，所以直接调用即可
        Userinfo userinfo = findByUsername(username);
        if(userinfo==null){
            return null;
        }
        //然后我们再到内存中比对密码
        //这样做可以提高数据库读写效率
        //数据库优化后期我也会提及
        if(userinfo.getPassword().equals(password)){
            //如果密码匹配，返回这个对象
            return userinfo;
        }else{
            return null;
        }
    }

    /**
     *
     * @param pageNo 第几页
     * @param pageSize 每页几条数据
     * @param record 按照什么条件查询
     * @return
     */
    @Override
    public PageInfo<Userinfo> page(int pageNo, int pageSize, Userinfo record) {
        //开启分页
        PageHelper.startPage(pageNo,pageSize);
        return new PageInfo<>(userinfoMapper.select(record));
    }
}
