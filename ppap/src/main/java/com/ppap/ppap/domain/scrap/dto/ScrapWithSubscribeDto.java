package com.ppap.ppap.domain.scrap.dto;

import com.ppap.ppap.domain.scrap.entity.Scrap;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public record ScrapWithSubscribeDto(
        Long curSubscribeId,
        List<ScrapDto> scraps
) {
    @Builder
    public ScrapWithSubscribeDto {
    }

    public record ScrapDto(
            Long contentId,
            String contentTitle,
            String link,
            LocalDateTime pubDate,
            Boolean isScrap
    ) {
        @Builder
        public ScrapDto {
        }
        public static ScrapDto of(Scrap scrap) {
            return ScrapDto.builder()
                    .contentId(scrap.getContent().getId())
                    .contentTitle(scrap.getContent().getTitle())
                    .link(scrap.getContent().getLink())
                    .pubDate(scrap.getContent().getPubDate())
                    .isScrap(true)
                    .build();
        }
    }

    public static ScrapWithSubscribeDto of(Long curSubscribeId, List<Scrap> scrapList) {
        List<ScrapDto> scraps = scrapList.stream().map(ScrapDto::of).toList();
        return ScrapWithSubscribeDto.builder()
                .curSubscribeId(curSubscribeId)
                .scraps(scraps)
                .build();
    }
}
