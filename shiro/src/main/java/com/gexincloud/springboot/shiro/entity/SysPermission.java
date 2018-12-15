package com.gexincloud.springboot.shiro.entity;

import javax.persistence.*;

@Table(name = "sys_permission")
public class SysPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 权限名称
     */
    @Column(name = "permission_name")
    private String permissionName;

    /**
     * 该权限对应拦截请求链接 #为不拦截
     */
    @Column(name = "permission_url")
    private String permissionUrl;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取权限名称
     *
     * @return permission_name - 权限名称
     */
    public String getPermissionName() {
        return permissionName;
    }

    /**
     * 设置权限名称
     *
     * @param permissionName 权限名称
     */
    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    /**
     * 获取该权限对应拦截请求链接 #为不拦截
     *
     * @return permission_url - 该权限对应拦截请求链接 #为不拦截
     */
    public String getPermissionUrl() {
        return permissionUrl;
    }

    /**
     * 设置该权限对应拦截请求链接 #为不拦截
     *
     * @param permissionUrl 该权限对应拦截请求链接 #为不拦截
     */
    public void setPermissionUrl(String permissionUrl) {
        this.permissionUrl = permissionUrl;
    }
}