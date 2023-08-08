package com.ppap.ppap.domain.user.utils.kakao;

import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception400;
import com.ppap.ppap._core.exception.Exception500;
import com.ppap.ppap.domain.user.dto.oauth.kakao.KakaoUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class KakaoRestTemplate {
    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeaders httpHeaders = new HttpHeaders();
    private final String KAKAO_URL = "https://kapi.kakao.com/v2/user/me?property_keys=[\"kakao_account.email\"]";

    @Value("${oauth.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth.kakao.secret}")
    private String kakaoSecret;


    public KakaoUserInfo getKakaoUserInfo(String token){
        httpHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        try {
            ResponseEntity<KakaoUserInfo> responseEntity = restTemplate.exchange(
                    KAKAO_URL,
                    HttpMethod.GET,
                    httpEntity,
                    KakaoUserInfo.class
            );
            return responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            if(e.getStatusCode().value() == 401)
                throw new Exception400(BaseExceptionStatus.KAKAO_TOKEN_INVALID);

            log.error(e.getMessage());
            throw new Exception500(BaseExceptionStatus.KAKAO_API_CONNECTION_ERROR);
        }
    }
}
