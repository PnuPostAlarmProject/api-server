package com.ppap.ppap.domain.scrap.service;

import com.ppap.ppap.domain.scrap.dto.ScrapFindByContentIdDto;
import com.ppap.ppap.domain.scrap.entity.Scrap;
import com.ppap.ppap.domain.scrap.repository.ScrapJpaRepository;
import com.ppap.ppap.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
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
                .map(scrap -> ScrapFindByContentIdDto.of(scrap, scrap.getUser(), scrap.getContent()))
                .toList();
    }

}
