package com.ppap.ppap.domain.user.dto;

public record LoginMemberResponseDto(
        String accessToken,
        String refreshToken
) {

    public static LoginMemberResponseDto of(String accessToken, String refreshToken) {
        return new LoginMemberResponseDto(accessToken, refreshToken);
    }
}
