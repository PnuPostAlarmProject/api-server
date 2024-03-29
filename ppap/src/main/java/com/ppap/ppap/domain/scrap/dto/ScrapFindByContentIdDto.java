package com.ppap.ppap.domain.scrap.dto;

import com.ppap.ppap.domain.scrap.entity.Scrap;

import lombok.Builder;

public record ScrapFindByContentIdDto(
        Long id,
        Long contentId,
        Long userId
) {
    @Builder
    public ScrapFindByContentIdDto {
    }

    public static ScrapFindByContentIdDto of(Scrap scrap) {
        return ScrapFindByContentIdDto.builder()
                .id(scrap.getId())
                .userId(scrap.getUser().getId())
                .contentId(scrap.getContent().getId())
                .build();
    }

}
