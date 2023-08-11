package com.ppap.ppap.domain.notice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubscribeUpdateRequestDto(
        @NotBlank String title,
        String noticeLink
) {
}
