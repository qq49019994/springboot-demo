# 第四个Springboot项目- shiro权限控制

##Shiro介绍

Apache Shiro是一个**强大**且**易用**的**Java安全框架**,执行身份验证、授权、密码和会话管理。使用Shiro的易于理解的API,您可以快速、轻松地获得任何应用程序,从最小的移动应用程序到最大的网络和企业应用程序。



- 软件名称

  Apache Shiro

- 开发商

  Apache

- 性    质

  Java安全框架

**主要功能**

三个核心组件：Subject, SecurityManager 和 Realms.

Subject：即“当前操作用户”。但是，在Shiro中，Subject这一概念并不仅仅指人，也可以是第三方进程、后台帐户（Daemon Account）或其他类似事物。它仅仅意味着“当前跟软件交互的东西”。但考虑到大多数目的和用途，你可以把它认为是Shiro的“用户”概念。
　　Subject代表了当前用户的安全操作，SecurityManager则管理所有用户的安全操作。
　　SecurityManager：它是Shiro框架的核心，典型的Facade模式，Shiro通过SecurityManager来管理内部组件实例，并通过它来提供安全管理的各种服务。
　　Realm： Realm充当了Shiro与应用安全数据间的“桥梁”或者“连接器”。也就是说，当对用户执行认证（登录）和授权（访问控制）验证时，Shiro会从应用配置的Realm中查找用户及其权限信息。
　　从这个意义上讲，Realm实质上是一个安全相关的DAO：它封装了数据源的连接细节，并在需要时将相关数据提供给Shiro。当配置Shiro时，你必须至少指定一个Realm，用于认证和（或）授权。配置多个Realm是可以的，但是至少需要一个。
　　Shiro内置了可以连接大量安全数据源（又名目录）的Realm，如LDAP、关系数据库（JDBC）、类似INI的文本配置资源以及属性文件等。如果缺省的Realm不能满足需求，你还可以插入代表自定义数据源的自己的Realm实现。



ShiroFilterFactoryBean：是个拦截器，在请求进入控制层前将其拦截，需要将安全管理器SecurityManager注入其中

SecurityManager：安全管理器，需要将自定义realm注入其中，以后还可以将缓存、remeberme等注入其中

自定义reaml：认证授权会执行它，需要自己写



## 引入Shiro依赖

~~~
        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-all</artifactId>
            <version>1.4.0</version>
            <type>pom</type>
        </dependency>
~~~

## 数据库设计

1.用户组表

~~~
CREATE TABLE `demo-shiro`.`sys_role`(  
  `id` INT(10) NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(20) NOT NULL COMMENT '用户组名称',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UNIQUE_ROLE_NAME` (`role_name`)
);

~~~



2.权限表

~~~
CREATE TABLE `demo-shiro`.`sys_permission`(  
  `id` INT(10) NOT NULL AUTO_INCREMENT,
  `permission_name` VARCHAR(20) NOT NULL COMMENT '权限名称',
  `permission_url` VARCHAR(50) NOT NULL DEFAULT '#' COMMENT '该权限对应拦截请求链接 #为不拦截',
  PRIMARY KEY (`id`)
);
ALTER TABLE `demo-shiro`.`sys_permission`   
  ADD  UNIQUE INDEX `UNIQUE_PERMISSION_NAME` (`permission_name`);

~~~

3.用户表

~~~
CREATE TABLE `demo-shiro`.`sys_user`(  
  `id` INT(10) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(20) NOT NULL COMMENT '账号',
  `password` CHAR(32) NOT NULL COMMENT '密码',
  `role_id` INT(10) NOT NULL COMMENT '所属用户组',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UNIQUE_USERNAME` (`username`)
);

~~~

4.用户组所拥有权限表

~~~
CREATE TABLE `demo-shiro`.`sys_role_permission`(  
  `id` INT(10) NOT NULL AUTO_INCREMENT,
  `role_id` INT(10) NOT NULL COMMENT '用户组ID',
  `permission_id` INT(10) NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`)
);

~~~



## springboot 集成shiro

1.配置ShiroConfig

~~~
package com.gexincloud.springboot.shiro.shiro;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    //配置自定义的密码比较器
    //这里我们改为MD5+salt
    @Bean
    public CredentialsMatcher credentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法:这里使用sha算法;
        hashedCredentialsMatcher.setHashIterations(1024);//散列的次数，这里加密1024次
        hashedCredentialsMatcher.setHashSalted(true);
        return hashedCredentialsMatcher;
    }
    /**
     * ShiroFilterFactoryBean 处理拦截资源文件问题。
     * 注意：单独一个ShiroFilterFactoryBean配置是或报错的，因为在初始化ShiroFilterFactoryBean的时候需要注入：SecurityManager
     * Filter Chain定义说明 1、一个URL可以配置多个Filter，使用逗号分隔 2、当设置多个过滤器时，全部验证通过，才视为通过
     * 3、部分过滤器可指定参数，如perms，roles
     */
    //
    @Bean
    public ShiroFilterFactoryBean shirFilter(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 必须设置 SecurityManager
        //org.apache.shiro.web.mgt.DefaultWebSecurityManager;
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //如果没有登录，访问需要权限控制的链接强制跳转的地址 /
        shiroFilterFactoryBean.setLoginUrl("/");
        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/main");
        // 未授权界面;
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        // 拦截器.
        //这里必须是有序的LinkedHashMap
        //shiro权限控制  anon -- 指定用户组 -- authc
        //anon 不拦截 即不需要登录就能访问的地址
        //指定用户组 只有某个用户组可以用
        //authc 只要是登录过的就能访问
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        // 配置不会被拦截的链接 顺序判断
        //静态资源不被拦截
        filterChainDefinitionMap.put("/static/**", "anon");
        //登录页不拦截
        filterChainDefinitionMap.put("/", "anon");
        //注册页不拦截
        filterChainDefinitionMap.put("/regist", "anon");
        //登录动作不拦截
        filterChainDefinitionMap.put("/login", "anon");
        //注册动作不拦截
        filterChainDefinitionMap.put("/sysUser/insert", "anon");
        //注册验证不拦截
        filterChainDefinitionMap.put("/sysUser/validate", "anon");
        //其他方法都需要拦截
        filterChainDefinitionMap.put("/**", "authc");
        // 配置退出过滤器,其中的具体的退出代码Shiro已经替我们实现了
        //退出登录
        filterChainDefinitionMap.put("/logout", "logout");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 设置realm.
        securityManager.setRealm(myShiroRealm());
        return securityManager;
    }
    /**
     * 身份认证realm; (这个需要自己写，账号密码校验；权限等)
     * @return
     */
    @Bean
    public MyShiroRealm myShiroRealm() {
        MyShiroRealm myShiroRealm = new MyShiroRealm();
        myShiroRealm.setCredentialsMatcher(credentialsMatcher());
        return myShiroRealm;
    }
    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }
}

~~~

2.编写MyShiroRealm

~~~
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

~~~



3.编写SysUserService实现登录业务

~~~
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
}
~~~

4.编写Shiro加密的工具类，用于注册时数据库存储密码

~~~
package com.gexincloud.springboot.shiro.util;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

public class ShiroMd5Util {

    public static String getMd5Str(String password, String salt) {
        String hashAlgorithmName = "MD5";
        Object credentials = password;
        Object salt1 = ByteSource.Util.bytes(salt);
        int hashIterations = 1024;
        Object result = new SimpleHash(hashAlgorithmName, credentials, salt1, hashIterations);
        return String.valueOf(result);
    }
}

~~~

## 编写注册页面

由于thymeleaf是按照严格的html5执行，这很显然不科学，所以我们要引入一些第三方依赖和配置实现thymeleaf的低要求渲染

例如<link href="" /> 是严格标准

我们写代码时 <link href="" > 

引入依赖

~~~
        <dependency>
            <groupId>net.sourceforge.nekohtml</groupId>
            <artifactId>nekohtml</artifactId>
            <version>1.9.22</version>
        </dependency>
~~~

application.yml

~~~
spring:
    thymeleaf:
    cache: false # 开发时关闭缓存,不然没法看到实时页面
    mode: LEGACYHTML5 # 用非严格的 HTML
    encoding: UTF-8
    servlet:
      content-type: text/html
~~~

页面代码

~~~
<!DOCTYPE html SYSTEM "http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-spring4-4.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>AdminLTE 2 | Log in</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!-- Bootstrap 3.3.7 -->
    <link rel="stylesheet" href="http://localhost/AdminLTE-2.4.5/bower_components/bootstrap/dist/css/bootstrap.min.css">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="http://localhost/AdminLTE-2.4.5/bower_components/font-awesome/css/font-awesome.min.css">
    <!-- Ionicons -->
    <link rel="stylesheet" href="http://localhost/AdminLTE-2.4.5/bower_components/Ionicons/css/ionicons.min.css">
    <!-- Theme style -->
    <link rel="stylesheet" href="http://localhost/AdminLTE-2.4.5/dist/css/AdminLTE.min.css">
    <!-- iCheck -->
    <link rel="stylesheet" href="http://localhost/AdminLTE-2.4.5/plugins/iCheck/square/blue.css">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

    <!-- Google Font -->
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,600,700,300italic,400italic,600italic">
</head>
<body class="hold-transition login-page">
<div class="login-box">
    <div class="login-logo">
        <a href="../../index2.html"><b>Admin</b>LTE</a>
    </div>
    <!-- /.login-logo -->
    <div class="login-box-body">
        <p class="login-box-msg">注册页面</p>
        <!-- 我们对div的text属性赋值 -->
        <!-- 具体用法后面会介绍 -->
        <form action="/userinfo/insert" method="post">
            <div class="form-group has-feedback">
                <!-- required 必填-->
                <input type="text" class="form-control" placeholder="请输入账号" name="username" required>
                <span class="glyphicon glyphicon-envelope form-control-feedback"></span>
            </div>
            <div class="form-group has-feedback">
                <input type="password" class="form-control" placeholder="请输入密码" name="password" required>
                <span class="glyphicon glyphicon-lock form-control-feedback"></span>
            </div>
            <div class="form-group has-feedback">
                <input type="password" class="form-control" placeholder="请再次输入密码" name="password2" required>
                <span class="glyphicon glyphicon-lock form-control-feedback"></span>
            </div>
            <input type="hidden" name="roleId" value="1">
            <div class="row">
                <!-- /.col -->
                <div class="col-xs-4">
                    <button type="submit" class="btn btn-primary btn-block btn-flat pull-right">注册</button>
                </div>
                <!-- /.col -->
            </div>
        </form>
        <!-- /.social-auth-links -->

        <a href="/" class="text-center">已有账号，马上去登录</a>

    </div>
    <!-- /.login-box-body -->
</div>
<!-- /.login-box -->

<!-- jQuery 3 -->
<script src="http://localhost/AdminLTE-2.4.5/bower_components/jquery/dist/jquery.min.js"></script>
<!-- Bootstrap 3.3.7 -->
<script src="http://localhost/AdminLTE-2.4.5/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
<!-- iCheck -->
<script src="http://localhost/AdminLTE-2.4.5/plugins/iCheck/icheck.min.js"></script>
<script>
    $(function () {
        $('input').iCheck({
            checkboxClass: 'icheckbox_square-blue',
            radioClass: 'iradio_square-blue',
            increaseArea: '20%' /* optional */
        });
    });
</script>
</body>
</html>

~~~

## 使用jQuery.validate实现数据验证

引入js和语言包

~~~
<script src="http://static.runoob.com/assets/jquery-validation-1.14.0/dist/jquery.validate.min.js"></script>
<script src="http://static.runoob.com/assets/jquery-validation-1.14.0/dist/localization/messages_zh.js"></script>
~~~

第一个验证，两次密码一致

第二个验证，账号不能重复

Ajax提交

~~~
$(document).ready(function(){
    $("form").validate({
        rules:{
            password:{
                equalTo:"[name=password2]"
            },username:{
                remote:{
                    url:'/sysUser/validate',
                    type:'post',
                    data:{
                        username:function(){
                            return $("[name=username]").val();
                        }
                    }
                }
            }
        },submitHandler:function (form) {
            $(":submit").attr("disabled", "disabled");
            $(form).ajaxSubmit({
                success: function (result) {
                    if(result){
                        alert("提交成功");
                        window.location.href="/";
                    }else{
                        alert("提交失败");
                    }
                    $(":submit").removeAttr("disabled");
                }
            })
            return false;
        }
    });
})
~~~

注册功能

service

~~~
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
~~~

controller

~~~
    @ResponseBody
    @PostMapping(value = "/insert")
    public boolean insert(SysUser sysUser){
        //注册方法
        //前面教程我们提到过不能把所有验证都交给前端去验证，因为有人会可能绕过这个验证，所以这里我们做二次验证
        SysUser record = new SysUser();
        record.setUsername(sysUser.getUsername());
        if(sysUserService.list(record).isEmpty()){
            sysUser.setPassword(ShiroMd5Util.getMd5Str(sysUser.getPassword(),sysUser.getUsername()));
            //插入方法会返回int的值，代表XX行数据被更改
            //0行数据被更改肯定是插入失败了，虽然没sql错误
            try {
                return sysUserService.insert(sysUser)>0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
~~~

## 页面按钮控制

引入依赖

~~~
<dependency>
    <groupId>com.github.theborakompanioni</groupId>
    <artifactId>thymeleaf-extras-shiro</artifactId>
    <version>2.0.0</version> 
</dependency>
~~~

ShiroConfig加入

~~~
    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }
~~~

页面加入

~~~
xmlns:shiro="http://www.pollix.at/thymeleaf/shiro"
~~~

~~~
<!DOCTYPE html SYSTEM "http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-spring4-4.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org"
      xmlns:shiro="http://www.pollix.at/thymeleaf/shiro">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
属于某个用户组时可见
<button type="button" shiro:hasRole="admin">admin</button>
<br/>
属于某个几个时可见
<button type="button" shiro:hasAnyRoles="admin,user">admin,user</button>
<br/>
拥有某个权限时可见
<button type="button" shiro:hasPermission="删除用户">删除用户</button>
<br/>
拥有某个几个权限时可见
<button type="button" shiro:hasAnyPermissions="删除用户,修改用户">删除用户,修改用户</button>
<br/>
未登录时可见
<button type="button" shiro:guest>未登录</button>
<br/>
已登录时可见
<button type="button" shiro:authenticated>已登录</button>
<br/>
不属于某个用户组时可见
<button type="button" shiro:lacksRole="admin">admin</button>
<br/>
不拥有某个权限时可见
<button type="button" shiro:lacksPermission="删除用户">删除用户</button>
<br/>
</body>
</html>
~~~

