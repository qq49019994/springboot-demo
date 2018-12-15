package com.gexincloud.springboot.shiro.service.impl;

import com.gexincloud.springboot.shiro.entity.SysRole;
import com.gexincloud.springboot.shiro.mapper.SysRoleMapper;
import com.gexincloud.springboot.shiro.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysRoleServiceImpl implements SysRoleService {
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Override
    public SysRole findById(int id) {
        return sysRoleMapper.selectByPrimaryKey(id);
    }
}
