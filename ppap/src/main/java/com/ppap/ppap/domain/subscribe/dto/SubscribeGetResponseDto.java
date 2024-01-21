package com.ppap.ppap.domain.subscribe.dto;

import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import lombok.Builder;

public record SubscribeGetResponseDto(
        Long subscribeId,
        String title,
        String noticeLink,
        String rssLink,
        Boolean isActive
) {
    @Builder
    public SubscribeGetResponseDto {
    }

    public static SubscribeGetResponseDto from(Subscribe subscribe) {
        return SubscribeGetResponseDto.builder()
                .subscribeId(subscribe.getId())
                .title(subscribe.getTitle())
                .noticeLink(subscribe.getNoticeLink())
                .rssLink(subscribe.getNotice().getLink())
                .isActive(subscribe.getIsActive())
                .build();
    }
}
