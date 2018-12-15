package com.gexincloud.springboot.shiro.service;

import com.gexincloud.springboot.shiro.entity.SysRolePermission;

import java.util.List;

public interface SysRolePermissionService {
    List<SysRolePermission> listByRoleId(int roleId);
}
