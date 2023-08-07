package com.ppap.ppap.domain.user.dto.oauth.kakao;

import com.ppap.ppap.domain.user.Entity.constant.Provider;
import com.ppap.ppap.domain.user.dto.oauth.OauthUserInfo;

import java.time.LocalDateTime;

public record KakaoUserInfo(
        Long id,
        LocalDateTime connected_at,
        KakaoAccount kakao_account
) implements OauthUserInfo {

    public record KakaoAccount(
            boolean has_email,
            boolean email_needs_agreement,
            boolean is_email_valid,
            boolean is_email_verified,
            String email
    ){
    }

    @Override
    public String email() {return this.kakao_account.email();}
    @Override
    public Provider provider() {return Provider.PROVIDER_KAKAO;}
    @Override
    public Long id() {return this.id;}
}
