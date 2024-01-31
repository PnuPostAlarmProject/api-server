package com.ppap.ppap._core.security;


import com.ppap.ppap._core.exception.Exception401;
import com.ppap.ppap._core.exception.Exception403;
import com.ppap.ppap._core.utils.FilterResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtExceptionFilter jwtExceptionFilter;
    private final FilterResponseUtils filterResponseUtils;

    @Bean
    public PasswordEncoder passwordEncoder() {return PasswordEncoderFactories.createDelegatingPasswordEncoder();}

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }

    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

//            builder.addFilter(new CustomUsernamePasswordAuthenticationFilter(authenticationManager));
            builder.addFilter(new JwtAuthenticationFilter(authenticationManager));
            builder.addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);
            super.configure(builder);
        }
    }

    // 스프링 3.1 (시큐리티 6.1)부터 코드를 대부분 람다식으로 바꿔줘야한다.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 해제
        http.csrf(CsrfConfigurer::disable);

        // iframe거부
        http.headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        // cors 재설정
        http.cors((cors) -> cors.configurationSource(corsConfigurationSource()));

        // jSessionID 사용 거부(세션 사용X)
        http.sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 폼 로그인 해제 (UsernamePasswordAuthenticationFilter 비활성화)
        http.formLogin(FormLoginConfigurer::disable);

        // 폼 로그아웃 해제
        http.logout(LogoutConfigurer::disable);

        // 로그인 인증창 비활성화
        http.httpBasic(HttpBasicConfigurer::disable);

        // 새로 만든 필터 등록
        http.apply(new CustomSecurityFilterManager());

        // 인증 실패 처리
        http.exceptionHandling((exceptionHandling) ->
                exceptionHandling.authenticationEntryPoint((request, response, authException) -> {
                    log.warn("인증되지 않은 사용자가 자원에 접근하려 합니다 : " + authException.getMessage());
                    filterResponseUtils.unAuthorizationRepsonse(response, new Exception401("인증되지 않은 사용자입니다."));
                })
        );

        // 권한 실패 처리
        http.exceptionHandling((exceptionHandling) ->
                exceptionHandling.accessDeniedHandler((request, response, accessDeniedException) -> {
                    log.warn("권한이 없는 사용자가 자원에 접근하려 합니다 : "+accessDeniedException.getMessage());
                    filterResponseUtils.forbiddenResponse(response, new Exception403("권한이 없는 사용자입니다."));
                })
        );

        // 인증, 권한 필터 설정
        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(new AntPathRequestMatcher("/auth/kakao/**"),
                                new AntPathRequestMatcher("/auth/reissue")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/auth/**"),
                                new AntPathRequestMatcher("/subscribe/**"),
                                new AntPathRequestMatcher("/scrap/**"),
                                new AntPathRequestMatcher("/content/**"),
                                new AntPathRequestMatcher("/univ/**")).authenticated()
                        .anyRequest().permitAll()
        );

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*"); // GET, POST, PUT, DELETE 허용
        configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용
        configuration.setAllowCredentials(true); // 클라이언트에서 쿠키 요청 허용
        configuration.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
