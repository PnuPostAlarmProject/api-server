package com.ppap.ppap.domain.subscribe.service;

import java.util.Collection;
import java.util.List;

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

    public Notice save(String link) {
        return noticeJpaRepository.save(Notice.of(link));
    }

    public Notice save(Notice notice) {
        return noticeJpaRepository.save(notice);
    }

    public void saveAllAndFlush(Collection<Notice> noticeList) {
        noticeJpaRepository.saveAllAndFlush(noticeList);
    }

    public void updateAll(Collection<Notice> noticeList) {
        noticeJpaRepository.updateAll(noticeList);
    }
}
