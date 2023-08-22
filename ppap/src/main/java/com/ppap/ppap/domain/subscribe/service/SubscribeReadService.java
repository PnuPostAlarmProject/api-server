package com.ppap.ppap.domain.subscribe.service;

import com.ppap.ppap.domain.subscribe.dto.SubscribeGetResponseDto;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.subscribe.repository.SubscribeJpaRepository;
import com.ppap.ppap.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class SubscribeReadService {
    private final SubscribeJpaRepository subscribeJpaRepository;

    public List<SubscribeGetResponseDto> getSubscribe(User user) {
        List<Subscribe> subscribeList = subscribeJpaRepository.findByUserId(user.getId());

        return subscribeList.stream()
                .map(SubscribeGetResponseDto::from)
                .toList();
    }

    public List<Subscribe> getSubscribeByNoticeId(Long noticeId) {
        return subscribeJpaRepository.findByNoticeId(noticeId);
    }
}