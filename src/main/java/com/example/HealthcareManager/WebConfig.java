package com.example.HealthcareManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	
//	@Value("${cors.allowed.origins}")//讀取proper裡的cors.allowed.origins設定
//	private String allowedOrigins;  
	
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://10.0.2.2") //全局配置
                .allowedMethods("*") // 允許的方法
                .allowCredentials(true)
                .allowedHeaders("*")
        		.exposedHeaders("access-control-allow-headers",
                        "access-control-allow-methods",
                        "access-control-allow-origin",
                        "access-control-max-age",
                        "X-Frame-Options");
        
    }
    
}
