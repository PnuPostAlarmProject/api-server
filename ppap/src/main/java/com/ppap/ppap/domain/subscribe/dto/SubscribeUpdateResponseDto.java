package com.ppap.ppap.domain.subscribe.dto;

import com.ppap.ppap.domain.subscribe.entity.Subscribe;

public record SubscribeUpdateResponseDto(
        Long subscribeId,
        String title,
        String noticeLink,
        Boolean isActive
) {
    public static SubscribeUpdateResponseDto from(Subscribe subscribe) {
        return new SubscribeUpdateResponseDto(subscribe.getId(), subscribe.getTitle(), subscribe.getNoticeLink(), subscribe.getIsActive());
    }
}
