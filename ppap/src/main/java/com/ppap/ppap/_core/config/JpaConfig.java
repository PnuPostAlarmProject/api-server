package com.ppap.ppap._core.config;

import com.ppap.ppap._core.security.CustomUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

// 물론 Application에 추가하면 편하지만 Spring에서도 추천하지 않는 방식이라고 한다.
@EnableJpaAuditing
@Configuration
public class JpaConfig {

    /**
     * 해당 코드는 @createdBy를 사용할 때 필요합니다.
     * 현재 코드에서는 필요하진 않습니다.
     */
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(CustomUserDetails.class::cast)
                .map(CustomUserDetails::getUsername);
    }
}
