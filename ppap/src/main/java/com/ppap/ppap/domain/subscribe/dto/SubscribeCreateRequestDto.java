package com.ppap.ppap.domain.subscribe.dto;

import jakarta.validation.constraints.NotBlank;

public record SubscribeCreateRequestDto(
        @NotBlank String title,
        @NotBlank String rssLink,
        String noticeLink
) {
}
