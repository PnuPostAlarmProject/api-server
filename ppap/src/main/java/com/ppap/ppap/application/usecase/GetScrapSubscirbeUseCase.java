package com.ppap.ppap.application.usecase;

import com.ppap.ppap.domain.scrap.dto.ScrapWithSubscribeDto;
import com.ppap.ppap.domain.scrap.service.ScrapReadService;
import com.ppap.ppap.domain.subscribe.service.SubscribeReadService;
import com.ppap.ppap.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;

@RequiredArgsConstructor
@Service
public class GetScrapSubscirbeUseCase {
    private final SubscribeReadService subscribeReadService;
    private final ScrapReadService scrapReadService;

    // 미완성
    public ScrapWithSubscribeDto execute(User user, Pageable pageable) {
        return null;
    }
}
