package com.ppap.ppap.domain.scrap.dto;

import com.ppap.ppap.domain.scrap.entity.Scrap;
import com.ppap.ppap.domain.subscribe.entity.Content;
import com.ppap.ppap.domain.user.entity.User;
import lombok.Builder;

public record ScrapFindByContentIdDto(
        Long id,
        Long contentId,
        Long userId
) {
    @Builder
    public ScrapFindByContentIdDto {
    }

    public static ScrapFindByContentIdDto of(Scrap scrap, User user, Content content) {
        return ScrapFindByContentIdDto.builder()
                .id(scrap.getId())
                .userId(user.getId())
                .contentId(content.getId())
                .build();
    }

}
