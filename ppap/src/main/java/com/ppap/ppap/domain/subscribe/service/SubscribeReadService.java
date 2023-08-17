package com.ppap.ppap.domain.subscribe.service;

import com.ppap.ppap.domain.subscribe.dto.SubscribeGetResponseDto;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.subscribe.repository.SubscribeRepository;
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
    private final SubscribeRepository subscribeRepository;

    public List<SubscribeGetResponseDto> getSubscribe(User user) {
        List<Subscribe> subscribeList = subscribeRepository.findByUserId(user.getId());

        return subscribeList.stream()
                .map(SubscribeGetResponseDto::from)
                .toList();
    }
}
