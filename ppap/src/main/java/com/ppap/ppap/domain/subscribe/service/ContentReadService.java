package com.ppap.ppap.domain.subscribe.service;

import com.ppap.ppap.domain.subscribe.entity.Content;
import com.ppap.ppap.domain.subscribe.repository.ContentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ContentReadService {
    private final ContentJpaRepository contentJpaRepository;


    public Set<Long> findDistinctNoticeId() {
        return contentJpaRepository.findAllDistinctNoticeId();
    }
}
