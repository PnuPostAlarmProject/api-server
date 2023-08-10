package com.ppap.ppap.domain.notice.service;

import com.ppap.ppap.domain.notice.entity.Notice;
import com.ppap.ppap.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeWriteService {
    private final NoticeRepository noticeRepository;

    public Notice save(String rssLink) {
        return noticeRepository.save(Notice.of(rssLink));
    }
}
