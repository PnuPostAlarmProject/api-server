package com.ppap.ppap.domain.subscribe.service;

import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.repository.NoticeJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeReadService {
    private final NoticeJpaRepository noticeJpaRepository;

    public Optional<Notice> findByRssLink(String rssLink) {
        return noticeJpaRepository.findByRssLink(rssLink);
    }

    public List<Notice> findAll() {
        return noticeJpaRepository.findAll();
    }
}
