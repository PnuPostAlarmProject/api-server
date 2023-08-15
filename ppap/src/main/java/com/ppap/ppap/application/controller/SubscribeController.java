package com.ppap.ppap.application.controller;

import com.ppap.ppap._core.security.CustomUserDetails;
import com.ppap.ppap._core.utils.ApiUtils;
import com.ppap.ppap.domain.subscribe.dto.SubscribeCreateRequestDto;
import com.ppap.ppap.domain.subscribe.dto.SubscribeGetResponseDto;
import com.ppap.ppap.domain.subscribe.dto.SubscribeUpdateRequestDto;
import com.ppap.ppap.domain.subscribe.dto.SubscribeUpdateResponseDto;
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
@RequestMapping("/subscribe")
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
    public ResponseEntity<?> get(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<SubscribeGetResponseDto> resultDtos = subscribeReadService.getSubscribe(userDetails.getUser());
        return ResponseEntity.ok(ApiUtils.success(resultDtos));
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
}
