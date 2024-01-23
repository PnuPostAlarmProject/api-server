package com.ppap.ppap.domain.subscribe.service;

import com.ppap.ppap._core.crawler.CrawlingData;
import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception500;
import com.ppap.ppap._core.crawler.RssData;
import com.ppap.ppap.domain.subscribe.entity.Content;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.repository.ContentJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ContentWriteService {
    private final ContentJpaRepository contentJpaRepository;

    /**
     * 스케줄러에서 받아온 Data를 저장하는 메소드
     */
    public void contentsSave(List<CrawlingData> crawlingDataList, Notice notice) {
        if (crawlingDataList.isEmpty()) return ;

        List<Content> content = crawlingDataList.stream()
                        .map(crawlingData -> Content.of(crawlingData, notice))
                        .toList();

        try {
            contentJpaRepository.saveAllBatch(content);
        } catch(Exception e) {
            log.error(e.getMessage());
            throw new Exception500(BaseExceptionStatus.CONTENT_SAVE_ERROR);
        }
    }

    public void contentAllSave(Map<Notice, List<CrawlingData>> crawlingNoticeGroup) {

        List<Content> contentList = crawlingNoticeGroup.entrySet().stream()
            .flatMap(entry -> entry.getValue().stream()
                .map(crawlingData -> Content.of(crawlingData, entry.getKey()))
                .toList()
                .stream()
            )
            .toList();

        if (contentList.isEmpty()) return;

        try {
            contentJpaRepository.saveAllBatch(contentList);
        } catch(Exception e) {
            log.error(e.getMessage());
            throw new Exception500(BaseExceptionStatus.CONTENT_SAVE_ERROR);
        }
    }
}
