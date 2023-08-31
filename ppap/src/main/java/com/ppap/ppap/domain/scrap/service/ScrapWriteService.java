package com.ppap.ppap.domain.scrap.service;

import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception400;
import com.ppap.ppap._core.exception.Exception403;
import com.ppap.ppap._core.exception.Exception404;
import com.ppap.ppap.domain.scrap.entity.Scrap;
import com.ppap.ppap.domain.scrap.repository.ScrapJpaRepository;
import com.ppap.ppap.domain.subscribe.entity.Content;
import com.ppap.ppap.domain.subscribe.service.ContentReadService;
import com.ppap.ppap.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ScrapWriteService {
    private final ScrapJpaRepository scrapJpaRepository;
    private final ContentReadService contentReadService;

    public void create(User user, Long contentId) {

        // content에 대한 예외는 contentReadService에서 하는 것이 좋아보임.
        Content content = contentReadService.findById(contentId);

        if (scrapJpaRepository.existsByUserIdAndContentId(user.getId(), contentId)) {
            throw new Exception400(BaseExceptionStatus.SCRAP_ALREADY_EXIST);
        }

        Scrap scrap = Scrap.of(user, content);

        scrapJpaRepository.save(scrap);
    }

    public void deleteByScrapId(User user, Long scrapId) {
        Scrap scrap = scrapJpaRepository.findById(scrapId).orElseThrow(
                () -> new Exception404(BaseExceptionStatus.SCRAP_NOT_FOUND));

        validateIsWriter(user, scrap);

        scrapJpaRepository.delete(scrap);
    }

    public void deleteByContentId(User user, Long contentId) {
        Scrap scrap = scrapJpaRepository.findByContentIdAndUserId(user.getId(), contentId).orElseThrow(
                () -> new Exception404(BaseExceptionStatus.SCRAP_NOT_FOUND)
        );

        scrapJpaRepository.delete(scrap);
    }

    private void validateIsWriter(User user, Scrap scrap) {
        if (!user.getId().equals(scrap.getUser().getId())) {
            throw new Exception403(BaseExceptionStatus.SCRAP_FORBIDDEN);
        }
    }
}
