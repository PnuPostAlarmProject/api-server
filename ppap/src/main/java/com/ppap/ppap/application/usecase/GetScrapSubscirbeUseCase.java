package com.ppap.ppap.application.usecase;

import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception404;
import com.ppap.ppap.domain.scrap.dto.ScrapWithSubscribeDto;
import com.ppap.ppap.domain.scrap.entity.Scrap;
import com.ppap.ppap.domain.scrap.service.ScrapReadService;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.subscribe.service.SubscribeReadService;
import com.ppap.ppap.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GetScrapSubscirbeUseCase {
    private final SubscribeReadService subscribeReadService;
    private final ScrapReadService scrapReadService;

    // 미완성
    public ScrapWithSubscribeDto execute(Optional<Long> subscribeId, User user, Pageable pageable) {
        // 등록중인 subscribe
        List<Subscribe> subscribeList = subscribeReadService.getSubscribeEntityList(user);
        subscribeList.sort(Comparator.comparing(Subscribe::getId));

        // 현재 클릭한 subscribeId
        Long curSubscribeId = subscribeId.orElse(subscribeList.get(0).getId());

        // 현재 클릭한 subscribe
        Subscribe curSubcribe = subscribeList.stream()
                .filter(subscribe -> subscribe.getId().equals(curSubscribeId))
                .findFirst().orElseThrow(() -> new Exception404(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND));

        List<Scrap> scrapList = scrapReadService.findByUserIdAndNoticeIdFetchJoinContent(
                user.getId(),
                curSubcribe.getNotice().getId(),
                pageable);


        return ScrapWithSubscribeDto.of(subscribeList, curSubscribeId, scrapList);
    }
}
