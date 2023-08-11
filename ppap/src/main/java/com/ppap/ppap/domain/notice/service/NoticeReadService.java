package com.ppap.ppap.domain.notice.service;

import com.ppap.ppap.domain.notice.entity.Notice;
import com.ppap.ppap.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeReadService {
    private final NoticeRepository noticeRepository;

    public Optional<Notice> findByRssLink(String rssLink) {
        return noticeRepository.findByRssLink(rssLink);
    }
}
