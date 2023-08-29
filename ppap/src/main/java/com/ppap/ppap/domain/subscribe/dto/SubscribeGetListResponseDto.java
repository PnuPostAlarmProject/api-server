package com.ppap.ppap.domain.subscribe.dto;

import com.ppap.ppap.domain.subscribe.entity.Subscribe;

public record SubscribeGetListResponseDto(
        Long subscribeId,
        String title,
        Boolean isActive
) {
    public static SubscribeGetListResponseDto of(Subscribe subscribe) {
        return new SubscribeGetListResponseDto(subscribe.getId(), subscribe.getTitle(), subscribe.getIsActive());
    }
}
