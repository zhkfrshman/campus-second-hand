package com.softwareengineering.team.campussecondhand.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置外部文件夹访问 - 上传的文件
        String uploadDir = System.getProperty("user.dir") + "/uploads/";
        System.out.println("配置上传目录访问: " + uploadDir);
        
        // 资源处理器配置
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadDir);
        
        // 确保静态资源可访问
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
                
        // 保留这个配置以支持可能的旧路径
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir);
    }
}