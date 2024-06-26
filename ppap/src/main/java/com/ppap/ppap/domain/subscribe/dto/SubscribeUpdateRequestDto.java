package com.ppap.ppap.domain.subscribe.dto;

import jakarta.validation.constraints.NotBlank;

public record SubscribeUpdateRequestDto(
        @NotBlank String title,
        String noticeLink
) {
}
