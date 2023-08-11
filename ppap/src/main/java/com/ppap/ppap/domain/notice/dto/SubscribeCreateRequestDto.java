package com.ppap.ppap.domain.notice.dto;

import jakarta.validation.constraints.NotBlank;

public record SubscribeCreateRequestDto(
        @NotBlank String title,
        @NotBlank String rssLink,
        String noticeLink
) {

}
