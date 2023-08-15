package com.ppap.ppap.application.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


@Profile("dev")
@RestController
@RequiredArgsConstructor
@RequestMapping("/dev/kakao")
public class KakaoTestController {
    @Value("${oauth.kakao.client-id}")
    private String clientId;

    @Value("${oauth.kakao.redirect}")
    private String redirect;

    @Value("${oauth.kakao.secret}")
    private String secret;

    private final ObjectMapper om;

    @GetMapping("/login")
    public void kakaoLogin(HttpServletResponse response) throws IOException {
        String url = String.format("https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=%s&redirect_uri=%s", clientId, redirect);
        response.sendRedirect(url);
    }

    @GetMapping("/login/redirect")
    public String kakaoLoginRedirect(@RequestParam("code") String code) throws JsonProcessingException {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "authorization_code");
        parameters.add("client_id", clientId);
        parameters.add("redirect_url", redirect);
        parameters.add("code", code);
        parameters.add("client_secret", secret);

        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        RestTemplate restTemplate = new RestTemplate();
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<?> httpRequestEntity = new HttpEntity<>(parameters, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl,httpRequestEntity, String.class);
            KakaoTokenResponseDto result = om.readValue(response.getBody(), KakaoTokenResponseDto.class);
            return result.access_token();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "failed";
    }

    private record KakaoTokenResponseDto(
        String token_type,
        String access_token,
        String id_token,
        Long expires_in,
        String refresh_token,
        Integer refresh_token_expires_in,
        String scope
    ){
        @Builder
        private KakaoTokenResponseDto {
        }
    }
}