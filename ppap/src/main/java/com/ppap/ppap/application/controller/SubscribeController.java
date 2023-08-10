package com.ppap.ppap.application.controller;

import com.ppap.ppap._core.security.CustomUserDetails;
import com.ppap.ppap._core.utils.ApiUtils;
import com.ppap.ppap.domain.notice.dto.SubscribeRequestDto;
import com.ppap.ppap.domain.notice.service.SubscribeReadService;
import com.ppap.ppap.domain.notice.service.SubscribeWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/subscribe")
public class SubscribeController {

    private final SubscribeWriteService subscribeWriteService;
    private final SubscribeReadService subscribeReadService;

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody SubscribeRequestDto requestDto,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        subscribeWriteService.create(requestDto, userDetails.getUser());
        return new ResponseEntity<>(ApiUtils.success("created"), HttpStatus.CREATED);
    }
}
