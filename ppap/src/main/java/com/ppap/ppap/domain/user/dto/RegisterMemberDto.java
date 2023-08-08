package com.ppap.ppap.domain.user.dto;

import lombok.Builder;

public record RegisterMemberDto(
        Long id
) {
    @Builder
    public RegisterMemberDto {
    }
}
