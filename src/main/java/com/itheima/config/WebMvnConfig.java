package com.itheima.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class WebMvnConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //设置映射访问文件路径
        log.info("开始进行静态资源映射");
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        //registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
        //registry.addResourceHandler("/employee/**").addResourceLocations("classpath:/employee/");
        //registry.addResourceHandler("/test/**").addResourceLocations("classpath:/test/");
    }
}
