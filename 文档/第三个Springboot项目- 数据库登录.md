#第三个Springboot项目- 数据库登录

## 使用tk.mybatis

tk.mybatis是第三方公司优化的mybatis，除了实现mybatis基础功能还集成了大量的实用插件

添加依赖 tk.mybatis 、分页查询、 Druid

~~~
        <dependency>
            <groupId>tk.mybatis</groupId>
            <artifactId>mapper-spring-boot-starter</artifactId>
            <version>2.1.0</version>
        </dependency>
        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper-spring-boot-starter</artifactId>
            <version>1.2.10</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.10</version>
        </dependency>
~~~



现在我们设计一个userinfo的表

~~~
CREATE TABLE `userinfo`(  
  `id` INT(10) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(20) NOT NULL,
  `password` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UNIQUE_USERNAME` (`username`)
);

~~~

由于账号是系统唯一，我们要在数据库中对它加入唯一索引，不能把所有的内容都寄托于业务的验证，因为有很多“大牛”会绕过这个验证去毁坏你的数据

根据58到家的数据库设计，所有字段必须是NOT NULL并且必要要指定默认值

## application.yml加入数据库连接

~~~
spring:
  datasource:
    druid:
      url: jdbc:mysql://127.0.0.1:3306/demo?useUnicode=true&characterEncoding=utf-8&useSSL=false
      username: root
      password: 123456
      initial-size: 1
      min-idle: 1
      max-active: 20
      test-on-borrow: true
      driver-class-name: com.mysql.cj.jdbc.Driver
mybatis:
    type-aliases-package: com.hoohui.platform.common.entity
    mapper-locations: classpath:mapper/*.xml
~~~

![1544803784803](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1544803784803.png)

mysql8出现这个问题是因为时区差异造成的，一般出现于mysql8安装在windows的情况

出现这个情况我们只需要在数据库连接加入serverTimezone=UTC 指定时区为国际标准时区即可

## 使用mybatis代码生成器一键生成 dao mapper entity

1.引入代码生成器插件

~~~
        <plugin>
            <groupId>org.mybatis.generator</groupId>
            <artifactId>mybatis-generator-maven-plugin</artifactId>
            <version>1.3.5</version>
            <configuration>
                <configurationFile>${basedir}/src/main/resources/generator/generatorConfig.xml</configurationFile>
                <overwrite>true</overwrite>
                <verbose>true</verbose>
            </configuration>
            <dependencies>
                <dependency>
                    <groupId>mysql</groupId>
                    <artifactId>mysql-connector-java</artifactId>
                    <version>${mysql.version}</version>
                </dependency>
                <dependency>
                    <groupId>tk.mybatis</groupId>
                    <artifactId>mapper</artifactId>
                    <version>3.4.4</version>
                </dependency>
            </dependencies>
        </plugin>
~~~

2.设置数据库连接，这里需要注意的是 必须使用mysql5.X版本的驱动，否则会把数据库中所有的表都生成

~~~
jdbc.driverClass=com.mysql.jdbc.Driver
jdbc.connectionURL=jdbc:mysql://ip:port/dbname?useUnicode=true&characterEncoding=utf-8&useSSL=false
jdbc.username=root
jdbc.password=123456
~~~



3.设置dao mapper entity生成的位置

在 `src/main/resources/generator/` 目录下创建 `generatorConfig.xml` 配置文件：

~~~
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>
    <!-- 引入数据库连接配置 -->
    <properties resource="jdbc.properties"/>

    <context id="Mysql" targetRuntime="MyBatis3Simple" defaultModelType="flat">
        <property name="beginningDelimiter" value="`"/>
        <property name="endingDelimiter" value="`"/>

        <!-- 配置 tk.mybatis 插件 -->
        <plugin type="tk.mybatis.mapper.generator.MapperPlugin">
            <property name="mappers" value="com.funtl.utils.MyMapper"/>
        </plugin>

        <!-- 配置数据库连接 -->
        <jdbcConnection
                driverClass="${jdbc.driverClass}"
                connectionURL="${jdbc.connectionURL}"
                userId="${jdbc.username}"
                password="${jdbc.password}">
        </jdbcConnection>

        <!-- 配置实体类存放路径 -->
        <javaModelGenerator targetPackage="com.funtl.hello.spring.boot.entity" targetProject="src/main/java"/>

        <!-- 配置 XML 存放路径 -->
        <sqlMapGenerator targetPackage="mapper" targetProject="src/main/resources"/>

        <!-- 配置 DAO 存放路径 -->
        <javaClientGenerator
                targetPackage="com.funtl.hello.spring.boot.mapper"
                targetProject="src/main/java"
                type="XMLMAPPER"/>

        <!-- 配置需要生成的表，% 代表所有 -->
        <table tableName="%">
            <!-- mysql 配置 -->
            <generatedKey column="id" sqlStatement="Mysql" identity="true"/>
        </table>
    </context>
</generatorConfiguration>
~~~

4.编写通用类，不能再启动入口的包下建立

~~~

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 自己的 Mapper
 * 特别注意，该接口不能被扫描到，否则会出错
 * <p>Title: MyMapper</p>
 * <p>Description: </p>
 *
 * @author Lusifer
 * @version 1.0.0
 * @date 2018/5/29 0:57
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
~~~

设置完以后maven面板的插件中会出现mybatis



![1544804337701](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1544804337701.png)

展开，点击第一个

![1544804359371](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1544804359371.png)

执行完以后项目目录



![1544804541623](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1544804541623.png)

## 编写一个功能，根据账号查找用户

service

~~~
    Userinfo findByUsername(String username);
~~~

serviceImpl

~~~
    @Override
    public Userinfo findByUsername(String username) {
        //这里我们从数据库设计上已知肯定是返回单条数据，所有使用selectOne
        //如果username传入空值，我们为了防止出现NNP，做一下判断
        if(StringUtils.isEmpty(username)){
            return null;
        }
        Userinfo record = new Userinfo();
        record.setUsername(username);
        return  userinfoMapper.selectOne(record);
    }
~~~

controller

~~~
    @ResponseBody
    @GetMapping(value = "/getByUsername")
    public Userinfo getByUsername(String username){
        return userinfoService.findByUsername(username);
    }
~~~

## 改造登录业务

service

~~~
Userinfo login(String username,String password);
~~~

serviceImpl

~~~
    @Override
    public Userinfo login(String username, String password) {
        //由于我们前面写了根据用户名查找功能，所以直接调用即可
        Userinfo userinfo = findByUsername(username);
        if(userinfo==null){
            return null;
        }
        //然后我们再到内存中比对密码
        //这样做可以提高数据库读写效率
        //数据库优化后期我也会提及
        if(userinfo.getPassword().equals(password)){
            //如果密码匹配，返回这个对象
            return userinfo;
        }else{
            return null;
        }
    }
~~~

controller

~~~
    @PostMapping(value = "/login")
    public String login(HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes, String username, String password, String rememberMe){
        //根据阿里的开发规范 .equals方法必须把字符串放前，变量放后，防止NNP的出现
        //调用service层的login方法
        Userinfo userinfo = userinfoService.login(username,password);
        if(userinfo!=null){
            //登录成功后把值放入session中
            request.getSession().setAttribute("userinfo",userinfo);
            //登录成功，跳转主页
            //跳转前我们先判断用户是不是勾选了记住我
            if("1".equals(rememberMe)){
                //Cookie以毫秒计数
                CookieUtils.setCookie(request,response,"token",userinfo.getUsername(),1000*60*60*24*15);
            }
            //转发到主页，防止出现多次请求问题
            return "redirect:/main";
        }else{
            //登录失败，返回错误信息并跳回登录页
            //转发回原页面，防止出现多次请求的问题
            redirectAttributes.addAttribute("error","账号或密码错误");
            return "redirect:/";
        }
    }
~~~



## 编写一个分页查询

service

~~~
PageInfo<Userinfo> page(int pageNo,int pageSize,Userinfo record);
~~~

serviceImpl

~~~
    /**
     *
     * @param pageNo 第几页
     * @param pageSize 每页几条数据
     * @param record 按照什么条件查询
     * @return
     */
    @Override
    public PageInfo<Userinfo> page(int pageNo, int pageSize, Userinfo record) {
        //开启分页
        PageHelper.startPage(pageNo,pageSize);
        return new PageInfo<>(userinfoMapper.select(record));
    }
~~~

controller

~~~
    //使用路径参数，使我们的请求更像restful风格
    @ResponseBody
    @GetMapping(value = "/page/{pageNo}/{pageSize}")
    public PageInfo<Userinfo> pageInfo(@PathVariable int pageNo,@PathVariable int pageSize,Userinfo record){
        return userinfoService.page(pageNo,pageSize,record);
    }
~~~

发送请求

http://localhost:8666/userinfo/page/1/1

从第1页查询，每页1条数据，无参数

返回结果

total：总条数

list：当前页数据

pageNum：当前第几页

pageSize：本页有几条数据

pages：一共有几页

~~~
{
	"total": 2,
	"list": [{
		"id": 1,
		"username": "admin",
		"password": "123456"
	}],
	"pageNum": 1,
	"pageSize": 1,
	"size": 1,
	"startRow": 1,
	"endRow": 1,
	"pages": 2,
	"prePage": 0,
	"nextPage": 2,
	"isFirstPage": true,
	"isLastPage": false,
	"hasPreviousPage": false,
	"hasNextPage": true,
	"navigatePages": 8,
	"navigatepageNums": [1, 2],
	"navigateFirstPage": 1,
	"navigateLastPage": 2
}
~~~

发送请求

http://localhost:8666/userinfo/page/1/2?password=123456

从第1页查找，每页2条数据，找到数据库中密码为123456的用户

返回结果

~~~
{
	"total": 3,
	"list": [{
		"id": 1,
		"username": "admin",
		"password": "123456"
	}, {
		"id": 2,
		"username": "wangtianfang",
		"password": "123456"
	}],
	"pageNum": 1,
	"pageSize": 2,
	"size": 2,
	"startRow": 1,
	"endRow": 2,
	"pages": 2,
	"prePage": 0,
	"nextPage": 2,
	"isFirstPage": true,
	"isLastPage": false,
	"hasPreviousPage": false,
	"hasNextPage": true,
	"navigatePages": 8,
	"navigatepageNums": [1, 2],
	"navigateFirstPage": 1,
	"navigateLastPage": 2
}
~~~

