package com.ppap.ppap.domain.subscribe.service;

import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.repository.NoticeJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeWriteService {
    private final NoticeJpaRepository noticeJpaRepository;

    public Notice save(String rssLink) {
        return noticeJpaRepository.save(Notice.of(rssLink));
    }
}
