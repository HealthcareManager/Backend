package com.example.HealthcareManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling//啟用定時器任務 模擬動態數據生成
public class HealthcareManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthcareManagerApplication.class, args);
	}

}