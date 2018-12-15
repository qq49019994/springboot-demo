package com.gexincloud.springboot.dblogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

//在启动类加入包扫描，否则程序不知道mapper在哪里
//这里一定要引入tk开头的包
@MapperScan("com.gexincloud.springboot.dblogin.mapper")
@SpringBootApplication
public class DbLoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbLoginApplication.class, args);
    }

}

