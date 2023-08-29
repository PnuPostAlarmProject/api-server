package com.ppap.ppap.domain.subscribe.service;

import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception404;
import com.ppap.ppap.domain.subscribe.entity.Content;
import com.ppap.ppap.domain.subscribe.repository.ContentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ContentReadService {
    private final ContentJpaRepository contentJpaRepository;


    public Set<Long> findDistinctNoticeId() {
        return contentJpaRepository.findAllDistinctNoticeId();
    }
    public List<Content> findByNoticeId(Long noticeId, Pageable pageable) {
        return contentJpaRepository.findByNoticeId(noticeId, pageable).toList();
    }

    public Content findById(Long contentId) {
        return contentJpaRepository.findById(contentId)
                .orElseThrow(() -> new Exception404(BaseExceptionStatus.CONTENT_NOT_FOUND));
    }
}
