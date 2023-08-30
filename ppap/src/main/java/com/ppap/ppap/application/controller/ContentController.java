package com.ppap.ppap.application.controller;

import com.ppap.ppap._core.security.CustomUserDetails;
import com.ppap.ppap._core.utils.ApiUtils;
import com.ppap.ppap.application.usecase.GetSubscribeContentUseCase;
import com.ppap.ppap.domain.subscribe.dto.SubscribeWithContentScrapDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/content")
public class ContentController {
    private final GetSubscribeContentUseCase getSubscribeContentUseCase;

    @GetMapping(value = {"", "/{subscribe_id}"})
    public ResponseEntity<?> getContentData(
            @PathVariable(required = false, name="subscribe_id") Optional<Long> subscribeId,
            @PageableDefault(size=10, page=0, sort = "pubDate", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        SubscribeWithContentScrapDto resultDto =
                getSubscribeContentUseCase.execute(subscribeId, userDetails.getUser(), pageable);

        return ResponseEntity.ok(ApiUtils.success(resultDto));
    }
}