package com.ppap.ppap.domain.subscribe.dto;

import com.ppap.ppap.domain.subscribe.entity.Content;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record SubscribeWithContentScrapDto(
        List<SubscribeDto> subscribes,
        Long curSubscribeId,
        List<ContentScrapDto>  contents
        ) {
    @Builder
    public SubscribeWithContentScrapDto {}

    public record SubscribeDto(
            Long subscribeId,
            String title
    ){
        @Builder
        public SubscribeDto {}

        public static SubscribeDto of(Subscribe subscribe){
            return SubscribeDto.builder()
                    .subscribeId(subscribe.getId())
                    .title(subscribe.getTitle())
                    .build();
        }
    }
    public record ContentScrapDto(
            Long contentId,
            String title,
            LocalDateTime pubDate,
            String link,
            Boolean isScraped
    ) {
        @Builder
        public ContentScrapDto {
        }

        public static ContentScrapDto of(Content content, Set<Long> scrapContentIdSet) {
            return ContentScrapDto.builder()
                    .contentId(content.getId())
                    .title(content.getTitle())
                    .pubDate(content.getPubDate())
                    .link(content.getLink())
                    .isScraped(scrapContentIdSet.contains(content.getId()))
                    .build();
        }
    }

    public static SubscribeWithContentScrapDto of(List<Subscribe> subscribeList,
                                                  Long curSubscribeId,
                                                  List<Content> contentList,
                                                  Set<Long> scrapContentIdSet) {

        List<SubscribeDto> subscribes = subscribeList.stream().map(SubscribeDto::of).toList();
        List<ContentScrapDto> contents = contentList.stream()
                .map(content -> ContentScrapDto.of(content, scrapContentIdSet)).toList();

        return SubscribeWithContentScrapDto.builder()
                .subscribes(subscribes)
                .curSubscribeId(curSubscribeId)
                .contents(contents)
                .build();
    }
}
