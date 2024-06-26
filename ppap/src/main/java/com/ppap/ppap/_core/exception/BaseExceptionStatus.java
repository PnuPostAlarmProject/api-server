package com.ppap.ppap._core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BaseExceptionStatus {

    // Common
    PAGE_SIZE_NOT_POSITIVE("페이지의 크기는 양수이어야 합니다.", 400),
    PAGE_SIZE_IS_UNDER_50("페이지의 크기는 50개 이하이어야 합니다.", 400),

    // User -> 나중에 custom status code가 있으면 좋을 듯 하다.
    USER_NOT_FOUND("회원이 존재하지 않습니다", 404),
    USER_ALREADY_EXIST("이미 존재하는 회원입니다.", 400),
    USER_SAVE_ERROR("회원 저장 중 문제가 발생했습니다", 500),

    // Kakao
    KAKAO_TOKEN_MISSING("카카오 accessToken을 입력해주세요", 400),
    KAKAO_TOKEN_INVALID("유효하지 않은 카카오 accessToken입니다.", 400),
    KAKAO_API_CONNECTION_ERROR("카카오 API 연동 중 문제가 발생했습니다", 500),


    // AccessToken, RefreshToken
    REFRESH_TOKEN_INVALID("유효하지 않은 토큰입니다.", 400),
    REFRESH_TOKEN_EXPIRED("만료된 토큰입니다.", 400),
    REFRESH_TOKEN_NOT_FOUND("존재하지 않는 토큰입니다.", 404),
    BLACKLIST_TOKEN_FOUNDED("블랙리스트에 등록된 토큰입니다.", 403),

    // RSS link
    RSS_LINK_INVALID("유효하지 않은 RSS 링크입니다.", 400),
    RSS_LINK_NOT_PNU("부산대 RSS 링크가 아닙니다.", 400),
    RSS_LINK_UNKNOWN_ERROR("RSS 링크 검증 중 알 수 없는 에러가 발생했습니다. 운영자에게 문의해주세요.", 500),
    RSS_LINK_NETWORK_ERROR("RSS 링크 검증 중 네트워크 에러가 발생했습니다. 잠시 후에 다시 시도해주세요.", 502),
    RSS_NOTICE_LINK_MISMATCH("RSS와 공지사항 링크 학과가 일치하지 않습니다.", 400),

    // Jsoup link
    JSOUP_LINK_UNKNOWN_ERROR("Jsoup 크롤링 중 알 수 없는 에러가 발생했습니다.", 500),
    JSOUP_NOT_REGISTRY_URL("현재 등록되지 않은 URL입니다.", 500),
    JSOUP_LINK_NETWORK_ERROR("Jsoup 크롤링 중 네트워크 에러가 발생했습니다.", 502),

    // subscribe
    SUBSCRIBE_ALREADY_EXIST("이미 존재하는 구독입니다.", 400),
    SUBSCRIBE_FORBIDDEN("허가되지 않은 접근입니다.", 403),
    SUBSCRIBE_NOT_FOUND("존재하지 않는 구독입니다.", 404),
    SUBSCRIBE_EMPTY("빈 구독 목록입니다.", 404),

    // content
    CONTENT_NOT_FOUND("존재하지 않는 공지사항 내용입니다.", 404),
    CONTENT_SAVE_ERROR("공지사항 내용을 저장하는 중 에러가 발생했습니다.", 500),

    // scrap
    SCRAP_ALREADY_EXIST("이미 존재하는 스크랩입니다.", 400),
    SCRAP_FORBIDDEN("허가되지 않은 접근입니다.", 403),
    SCRAP_NOT_FOUND("존재하지 않는 스크랩입니다.", 404),
    SCRAP_IS_EMPTY("스크랩에 데이터가 존재하지 않습니다.", 404),

    // device
    DEVICE_FCM_TOKEN_INVALID("유효하지 않은 FCM 토큰입니다.", 400),
    DEVICE_FCM_TOKEN_UNKNOWN_ERROR("FCM 토큰 검증 중 알 수 없는 에러가 발생했습니다.", 500);

    @Getter
    private final String message;

    @Getter
    private final int status;
}
