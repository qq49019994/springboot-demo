package com.gexincloud.springboot.shiro.shiro;

import com.gexincloud.springboot.shiro.entity.SysPermission;
import com.gexincloud.springboot.shiro.entity.SysRole;
import com.gexincloud.springboot.shiro.entity.SysRolePermission;
import com.gexincloud.springboot.shiro.entity.SysUser;
import com.gexincloud.springboot.shiro.service.SysPermissionService;
import com.gexincloud.springboot.shiro.service.SysRolePermissionService;
import com.gexincloud.springboot.shiro.service.SysRoleService;
import com.gexincloud.springboot.shiro.service.SysUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MyShiroRealm extends AuthorizingRealm {

    Boolean cachingEnabled = true;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysRolePermissionService sysRolePermissionService;
    @Autowired
    private SysPermissionService sysPermissionService;
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //在这里实现授权具体业务
        //1个账号对应1个用户组
        //1个用户组拥有多个权限
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        SysUser sysUser =(SysUser) principals.getPrimaryPrincipal();
        //根据用户获取用户组
        SysRole sysRole = sysRoleService.findById(sysUser.getRoleId());
        //根据用户组获取拥有权限
        SysRolePermission sysRolePermissionRecord = new SysRolePermission();
        sysRolePermissionRecord.setRoleId(sysRole.getId());
        info.addRole(sysRole.getRoleName());
        List<SysRolePermission> sysRolePermissionList = sysRolePermissionService.listByRoleId(sysRole.getId());
        //把权限放入shiro
        for (SysRolePermission sysRolePermission : sysRolePermissionList) {
            // 添加权限
            info.addStringPermission(sysPermissionService.findById(sysRolePermission.getPermissionId()).getPermissionName());
        }
        return info;
    }

    //登录验证器
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken authcToken) throws AuthenticationException {
        //获取基于用户名和密码的令牌
        //实际上这个authcToken是从LoginController里面currentUser.login(token)传过来的
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        //从token中获取username
        String username = token.getUsername();
        SysUser sysUser = sysUserService.login(username);//根据username从库中查询sysUser对象
        if (sysUser == null) {
            throw new AuthenticationException("用户不存在");
        }
        //进行认证，将正确数据给shiro处理
        //密码不用自己比对，AuthenticationInfo认证信息对象，一个接口，new他的实现类对象SimpleAuthenticationInfo
        /*	第一个参数随便放，可以放user对象，程序可在任意位置获取 放入的对象
         * 第二个参数必须放密码，
         * 第三个参数放 salt值
         * 第四个参数放 当前realm的名字，因为可能有多个realm
         */
        //为什么使用salt加密
        //正常MD5或者SHA加密，比如123456会加密成相等的值，这样如果一个用户的登录信息被人拦截，会撞库猜出其他人的密码
        //所以我们在加密的时候再给每一个用户设置一个不同的salt值，这样同一个用户密码123456在数据库中存储的内容也会不同
        //我们把登录账号序列化成salt值进行加密
        ByteSource salt = ByteSource.Util.bytes(sysUser.getUsername());
        AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(sysUser, sysUser.getPassword(),salt,this.getName());
        //清之前的授权信息
        super.clearCachedAuthorizationInfo(authcInfo.getPrincipals());
        //登录成功把sysUser对象存入session
        SecurityUtils.getSubject().getSession().setAttribute("sysUser", sysUser);
        return authcInfo;//返回给安全管理器，securityManager，由securityManager比对数据库查询出的密码和页面提交的密码
        //如果有问题，向上抛异常，一直抛到控制器
    }
}
