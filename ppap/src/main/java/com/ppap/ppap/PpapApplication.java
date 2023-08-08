package com.ppap.ppap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

// 유저가 자동 생성되는게 보기 싫어서 적용
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@ConfigurationPropertiesScan
public class PpapApplication {

    public static void main(String[] args) {
        SpringApplication.run(PpapApplication.class, args);
    }

}
