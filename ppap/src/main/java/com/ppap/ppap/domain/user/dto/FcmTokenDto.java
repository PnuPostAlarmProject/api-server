package com.ppap.ppap.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenDto(
        @NotBlank String fcmToken
) {

}
