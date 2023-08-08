package com.ppap.ppap.domain.user.Entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Provider {
    PROVIDER_NORMAL("normal", "일반 회원가입"),
    PROVIDER_KAKAO("kakao", "카카오");

    @Getter
    private final String provider;

    @Getter
    private final String decription;
}
