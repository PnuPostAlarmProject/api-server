package com.ppap.ppap._core.config;

import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.SAXBuilderEngine;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class HttpConnectionConfig {

    // Rest API 요청을 위한 Bean
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(5000)) //읽기시간초과, ms
                .setReadTimeout(Duration.ofMillis(5000))    //연결시간초과, ms
                .build();
    }

    // XML을 핵심으로 처리하기 위한 Bean
    // 싱글톤으로 만들면 멤버 변수에서 경합조건이 발생해 아래와 같이 Scope를 지정해줘야한다.
    @Scope("prototype")
    @Bean
    public SAXBuilder saxBuilder() {
        return new SAXBuilder();
    }
}
