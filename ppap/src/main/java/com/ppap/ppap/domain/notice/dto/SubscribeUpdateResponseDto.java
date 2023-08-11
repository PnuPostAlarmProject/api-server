package com.ppap.ppap.domain.notice.dto;

import com.ppap.ppap.domain.notice.entity.Notice;
import com.ppap.ppap.domain.notice.entity.Subscribe;

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
