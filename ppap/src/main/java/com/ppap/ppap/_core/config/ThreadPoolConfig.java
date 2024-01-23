package com.ppap.ppap._core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ForkJoinPool;

@Configuration
public class ThreadPoolConfig {
    private final int PARALLELISM = 10;

    @Bean
    public ForkJoinPool forkJoinPool() {
        return new ForkJoinPool(PARALLELISM);
    }
}
