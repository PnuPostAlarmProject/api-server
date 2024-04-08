package com.ppap.ppap.application.usecase;

import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception404;
import com.ppap.ppap.domain.base.utils.CursorRequest;
import com.ppap.ppap.domain.base.utils.PageCursor;
import com.ppap.ppap.domain.scrap.dto.ScrapWithSubscribeDto;
import com.ppap.ppap.domain.scrap.entity.Scrap;
import com.ppap.ppap.domain.scrap.service.ScrapReadService;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.subscribe.service.SubscribeReadService;
import com.ppap.ppap.domain.user.entity.User;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class GetScrapSubscirbeUseCase {
    private final SubscribeReadService subscribeReadService;
    private final ScrapReadService scrapReadService;

    public ScrapWithSubscribeDto execute(Optional<Long> subscribeId, User user, Pageable pageable) {
        Subscribe curSubscribe = getCurSubscribe(subscribeId, user);

        List<Scrap> scrapList = scrapReadService.findByUserIdAndNoticeIdFetchJoinContent(
            user.getId(),
            curSubscribe.getNotice().getId(),
            pageable);

        if (scrapList.isEmpty()) {
            throw new Exception404(BaseExceptionStatus.SCRAP_IS_EMPTY);
        }

        return ScrapWithSubscribeDto.of(curSubscribe.getId(), scrapList);
    }

    public PageCursor<ScrapWithSubscribeDto> executeV1(Optional<Long> subscribeId, User user, CursorRequest cursorRequest) {

        Pageable pageable = PageRequest.ofSize(cursorRequest.pageSize());
        Subscribe curSubscribe = getCurSubscribe(subscribeId, user);

        List<Scrap> scrapList = scrapReadService.findByUserIdAndNoticeIdFetchJoinContentCursor(
            user.getId(),
            curSubscribe.getNotice().getId(),
            cursorRequest,
            pageable);

        if (scrapList.isEmpty()) {
            throw new Exception404(BaseExceptionStatus.SCRAP_IS_EMPTY);
        }

        return new PageCursor<>(
            ScrapWithSubscribeDto.of(curSubscribe.getId(), scrapList),
            cursorRequest.next(scrapList.stream()
                .map(scrap -> scrap.getContent().getPubDate())
                .min(Comparator.comparing(Function.identity()))
                .orElse(CursorRequest.NONE_CURSOR)),
            scrapList.size());
    }

    // 현재 선택 중인 구독 가져오기
    private Subscribe getCurSubscribe(Optional<Long> subscribeId, User user) {
        // 등록중인 subscribe
        List<Subscribe> subscribeList = subscribeReadService.getSubscribeEntityList(user);

        // 현재 클릭한 subscribeId
        Long curSubscribeId = subscribeId.orElse(subscribeList.get(0).getId());

        return subscribeList.stream()
            .filter(subscribe -> subscribe.getId().equals(curSubscribeId))
            .findFirst().orElseThrow(() -> new Exception404(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND));
    }
}
