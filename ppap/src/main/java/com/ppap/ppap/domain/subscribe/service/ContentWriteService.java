package com.ppap.ppap.domain.subscribe.service;

import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception500;
import com.ppap.ppap._core.rss.RssData;
import com.ppap.ppap.domain.subscribe.entity.Content;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.repository.ContentJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ContentWriteService {
    private final ContentJpaRepository contentJpaRepository;

    /**
     * 스케줄러에서 받아온 rssData를 저장하는 메소드
     */
    public void contentsSave(List<RssData> rssDataList, Notice notice) {
        if (rssDataList.isEmpty()) return ;

        List<Content> content = rssDataList.stream()
                        .map(rssData -> Content.of(rssData, notice))
                        .toList();

        try {
            contentJpaRepository.saveAllBatch(content);
        } catch(Exception e) {
            log.error(e.getMessage());
            throw new Exception500(BaseExceptionStatus.CONTENT_SAVE_ERROR);
        }
    }
}
