package com.ppap.ppap.application.usecase;

import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception404;
import com.ppap.ppap.domain.scrap.dto.ScrapFindByContentIdDto;
import com.ppap.ppap.domain.scrap.service.ScrapReadService;
import com.ppap.ppap.domain.subscribe.dto.SubscribeWithContentScrapDto;
import com.ppap.ppap.domain.subscribe.entity.Content;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.subscribe.service.ContentReadService;
import com.ppap.ppap.domain.subscribe.service.SubscribeReadService;
import com.ppap.ppap.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GetSubscribeContentUseCase {
    private final SubscribeReadService subscribeReadService;
    private final ContentReadService contentReadService;
    private final ScrapReadService scrapReadService;

    /**
     * 컨텐츠 화면을 켰을 때 줄 서비스 로직
     */
    public SubscribeWithContentScrapDto execute(Optional<Long> subscribeId, User user, Pageable pageable) {
        // 등록중인 subscribes
        List<Subscribe> subscribeList = subscribeReadService.getSubscribeEntityList(user);
        subscribeList.sort(Comparator.comparing(Subscribe::getId));

        // 현재 클릭 중인 subscribeId
        Long curSubscribeId = subscribeId.orElse(subscribeList.get(0).getId());

        // 현재 클릭한 subscribe
        Subscribe curSubcribe = subscribeList.stream()
                .filter(subscribe -> subscribe.getId().equals(curSubscribeId))
                .findFirst()
                .orElseThrow(() -> new Exception404(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND));

        // 현재 클릭한 구독의 컨텐츠들
        // One To Many sql 1번 vs notice 1번, content 1번 -> 총 2번 (이거 선택)
        List<Content> contentList = contentReadService.findByNoticeId(curSubcribe.getNotice().getId(), pageable);

        // 유저, contentIds에 해당하는 스크랩 가져오기
        List<ScrapFindByContentIdDto> scrapList = scrapReadService.findByContentIds(user.getId(),
                contentList.stream().map(Content::getId).toList());

        Set<Long> scrapContentIdSet = scrapList.stream()
                .map(ScrapFindByContentIdDto::contentId)
                .collect(Collectors.toSet());

        return SubscribeWithContentScrapDto.of(curSubscribeId, contentList, scrapContentIdSet);
    }
}
