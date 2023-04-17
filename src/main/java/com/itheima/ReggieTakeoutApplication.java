package com.itheima;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement//因为操作多张表，记得加上事务相关注解：
@EnableCaching // 使用注解缓存 spring cache
public class ReggieTakeoutApplication {

    public static void main(String[] args) {

        SpringApplication.run(ReggieTakeoutApplication.class, args);
        log.info("项目启动成功");
    }

}
