package com.gexincloud.springboot.shiro.service.impl;

import com.gexincloud.springboot.shiro.entity.SysPermission;
import com.gexincloud.springboot.shiro.mapper.SysPermissionMapper;
import com.gexincloud.springboot.shiro.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysPermissionServiceImpl implements SysPermissionService {
    @Autowired
    private SysPermissionMapper sysPermissionMapper;
    @Override
    public SysPermission findById(int id) {
        return sysPermissionMapper.selectByPrimaryKey(id);
    }
}
