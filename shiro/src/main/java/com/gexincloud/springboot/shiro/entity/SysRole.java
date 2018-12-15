package com.gexincloud.springboot.shiro.entity;

import javax.persistence.*;

@Table(name = "sys_role")
public class SysRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户组名称
     */
    @Column(name = "role_name")
    private String roleName;

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
     * 获取用户组名称
     *
     * @return role_name - 用户组名称
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * 设置用户组名称
     *
     * @param roleName 用户组名称
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}