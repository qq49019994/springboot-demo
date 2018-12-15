#第一个Springboot项目-Hello World
##使用springboot构建器构造一个项目

选择模块：
​    1.devtools    开发者工具  用于热部署，即修改项目无需重启 直接发布
​    2.web         web模块 即这个项目是一个web应用
​    3.thymeleaf   视图层使用thymeleaf模板
​    4.test        test工程
​    
​    
构建完毕后在项目包名下会出现XXXApplication.java
此文件是程序入口
在springboot项目中 @SpringBootApplication注解表示程序入口

启动方式为：在此类 run as 或者 debug as

~~~
    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
    }
~~~
 resources 目录：
​    1.static --- 静态资源目录 如css,js,img
​    2.templates ---视图模板 如index.html（web默认入口为index.html）
​    3.application.yml 配置文件 官方推荐为yml文件
​    

现在编写一个index.html

此时启动，网页输入localhost:8080即可看到，一个简单的程序入口实现



##如何自定义程序入口？

​	在spring中我们使用@Controller注解实现前后台交互，这里同理

~~~
@Controller
public class IndexController {

    @RequestMapping(value = "/")
    public String index(){
        return "/main";
    }
}
~~~

此时我们发现入口已经变成了main

## 如何修改web程序端口

在application.yml中指定

~~~
server:
  port: 8888
~~~



