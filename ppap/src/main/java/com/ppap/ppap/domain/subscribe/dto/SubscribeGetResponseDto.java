package com.ppap.ppap.domain.subscribe.dto;

import com.ppap.ppap.domain.subscribe.entity.Subscribe;

public record SubscribeGetResponseDto(
        Long subscribeId,
        String title,
        Boolean isActive
) {
    public static SubscribeGetResponseDto from(Subscribe subscribe) {
        return new SubscribeGetResponseDto(subscribe.getId(), subscribe.getTitle(), subscribe.getIsActive());
    }
}
