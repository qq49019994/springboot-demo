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
