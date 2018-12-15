package com.gexincloud.springboot.shiro.service.impl;

import com.gexincloud.springboot.shiro.entity.SysPermission;
import com.gexincloud.springboot.shiro.entity.SysRolePermission;
import com.gexincloud.springboot.shiro.mapper.SysRolePermissionMapper;
import com.gexincloud.springboot.shiro.service.SysRolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SysRolePermissionImpl implements SysRolePermissionService {
    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;
    @Override
    public List<SysRolePermission> listByRoleId(int roleId) {
        SysRolePermission record = new SysRolePermission();
        record.setRoleId(roleId);
        return sysRolePermissionMapper.select(record);
    }
}
