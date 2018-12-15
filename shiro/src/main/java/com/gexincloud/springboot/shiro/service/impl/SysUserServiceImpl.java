package com.gexincloud.springboot.shiro.service.impl;

import com.gexincloud.springboot.shiro.entity.SysUser;
import com.gexincloud.springboot.shiro.mapper.SysUserMapper;
import com.gexincloud.springboot.shiro.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.List;

@Service
public class SysUserServiceImpl implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

    //由于我们的密码匹配交给了shiro，所以我们只需要实现根据username找到SysUser就行
    @Override
    public SysUser login(String username) {
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        SysUser record = new SysUser();
        record.setUsername(username);
        return sysUserMapper.selectOne(record);
    }

    @Override
    public List<SysUser> list(SysUser record) {
        return sysUserMapper.select(record);
    }

    @Override
    public int insert(SysUser sysUser)  throws SQLException {
        return sysUserMapper.insertSelective(sysUser);
    }
}
