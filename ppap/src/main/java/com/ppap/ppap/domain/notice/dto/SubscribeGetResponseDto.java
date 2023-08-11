package com.ppap.ppap.domain.notice.dto;

import com.ppap.ppap.domain.notice.entity.Subscribe;

public record SubscribeGetResponseDto(
        Long subscribeId,
        String title,
        Boolean isActive
) {
    public static SubscribeGetResponseDto from(Subscribe subscribe) {
        return new SubscribeGetResponseDto(subscribe.getId(), subscribe.getTitle(), subscribe.getIsActive());
    }
}
