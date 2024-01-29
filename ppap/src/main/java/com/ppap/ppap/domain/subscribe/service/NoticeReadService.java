package com.ppap.ppap.domain.subscribe.service;

import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.entity.constant.NoticeType;
import com.ppap.ppap.domain.subscribe.repository.NoticeJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeReadService {
    private final NoticeJpaRepository noticeJpaRepository;

    public Optional<Notice> findByLink(String link) {
        return noticeJpaRepository.findByLink(link);
    }

    public List<Notice> findAll() {
        return noticeJpaRepository.findAll();
    }

    public List<Notice> findByLinkIn(List<String> linkList){
        return noticeJpaRepository.findByLinkIn(linkList);
    }
}
