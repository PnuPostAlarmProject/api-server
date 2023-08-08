package com.ppap.ppap.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class ReissueDto{

    public record ReissueRequestDto (
            @NotNull String refreshToken
    ) {
        @Builder
        public ReissueRequestDto {
        }
    }

    public record ReissueResponseDto (
            String accessToken,
            String refreshToken
    ) {
        @Builder
        public ReissueResponseDto {
        }

        public static ReissueResponseDto of(String accessToken, String refreshToken) {
            return ReissueResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
    }
}
