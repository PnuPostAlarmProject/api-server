package com.ppap.ppap.domain.scrap.service;

import com.ppap.ppap.domain.scrap.dto.ScrapFindByContentIdDto;
import com.ppap.ppap.domain.scrap.entity.Scrap;
import com.ppap.ppap.domain.scrap.repository.ScrapJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ScrapReadService {
    private final ScrapJpaRepository scrapJpaRepository;

    public List<ScrapFindByContentIdDto> findByContentIds(Long userId, List<Long> contentIds) {
        List<Scrap> scrapList = scrapJpaRepository.findByContentIds(userId, contentIds);
        return scrapList.stream()
                .map(ScrapFindByContentIdDto::of)
                .toList();
    }

    public List<Scrap> findByUserIdAndNoticeIdFetchJoinContent(Long userId, Long noticeId, Pageable pageable) {
        return scrapJpaRepository.findByUserIdAndNoticeIdFetchJoinContent(userId, noticeId, pageable).toList();
    }
}
