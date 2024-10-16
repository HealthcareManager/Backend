package com.example.HealthcareManager;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /images/** 映射到文件系统目录 E:/HealthcareManager/src/main/resources/static/images/
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:E:/HealthcareManager/src/main/resources/static/images/");
    }
}
