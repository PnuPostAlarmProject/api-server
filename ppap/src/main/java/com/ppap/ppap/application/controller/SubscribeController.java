package com.ppap.ppap.application.controller;

import com.ppap.ppap._core.security.CustomUserDetails;
import com.ppap.ppap._core.utils.ApiUtils;
import com.ppap.ppap.domain.subscribe.dto.*;
import com.ppap.ppap.domain.subscribe.service.SubscribeReadService;
import com.ppap.ppap.domain.subscribe.service.SubscribeWriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v0/subscribe")
public class SubscribeController {

    private final SubscribeWriteService subscribeWriteService;
    private final SubscribeReadService subscribeReadService;

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody @Valid SubscribeCreateRequestDto requestDto,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        subscribeWriteService.create(requestDto, userDetails.getUser());
        return new ResponseEntity<>(ApiUtils.success("created"), HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<?> getList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<SubscribeGetListResponseDto> resultDtos = subscribeReadService.getSubscribeList(userDetails.getUser());
        return ResponseEntity.ok(ApiUtils.success(resultDtos));
    }

    @GetMapping("/{subscribe_id}")
    public ResponseEntity<?> get(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @PathVariable(name = "subscribe_id") Long subscribeId) {

        SubscribeGetResponseDto resultDto = subscribeReadService.getSubscribe(userDetails.getUser(), subscribeId);
        return ResponseEntity.ok(ApiUtils.success(resultDto));
    }

    @PostMapping("/{subscribe_id}")
    public ResponseEntity<?> update(@RequestBody @Valid SubscribeUpdateRequestDto requestDto,
                                    @PathVariable(name = "subscribe_id") Long subscribeId,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {

        SubscribeUpdateResponseDto resultDto = subscribeWriteService.update(requestDto, subscribeId, userDetails.getUser());
        return ResponseEntity.ok(ApiUtils.success(resultDto));
    }

    @PostMapping("/active/{subscribe_id}")
    public ResponseEntity<?> activeUpdate(@PathVariable(name = "subscribe_id") Long subscribeId,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {

        SubscribeUpdateResponseDto resultDto = subscribeWriteService.activeUpdate(subscribeId, userDetails.getUser());
        return ResponseEntity.ok(ApiUtils.success(resultDto));
    }

    @PostMapping("/delete/{subscribe_id}")
    public ResponseEntity<?> delete(@PathVariable(name="subscribe_id") Long subscribeId,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 구독 삭제 서비스 로직
        subscribeWriteService.delete(subscribeId, userDetails.getUser());
        return ResponseEntity.ok(ApiUtils.success(null));
    }
}
