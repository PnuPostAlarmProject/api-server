package com.ppap.ppap.application.controller;

import com.ppap.ppap._core.security.CustomUserDetails;
import com.ppap.ppap._core.utils.ApiUtils;
import com.ppap.ppap.application.usecase.GetSubscribeContentUseCase;
import com.ppap.ppap.domain.base.utils.CursorRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ContentController {
    private final GetSubscribeContentUseCase getSubscribeContentUseCase;

    @GetMapping(value = {"/v0/content", "/v0/content/{subscribe_id}"})
    public ResponseEntity<?> getContentData(
        @PathVariable(required = false, name="subscribe_id") Optional<Long> subscribeId,
        @PageableDefault(size=10, page=0, sort = "pubDate", direction = Sort.Direction.DESC) Pageable pageable,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        SubscribeWithContentScrapDto resultDto =
            getSubscribeContentUseCase.execute(subscribeId, userDetails.getUser(), pageable);

        return ResponseEntity.ok(ApiUtils.success(resultDto));
    }

    @GetMapping(value = {"/v1/content", "/v1/content/{subscribe_id}"})
    public ResponseEntity<?> getContentDataV1(
        @PathVariable(required = false, name="subscribe_id") Optional<Long>  subscribeId,
        @RequestParam(value="cursor", required = false) LocalDateTime cursor,
        @RequestParam(value="pageSize", required = false, defaultValue = "10") int pageSize,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CursorRequest cursorRequest = new CursorRequest(cursor, pageSize);
        return ResponseEntity.ok(ApiUtils.success(
            getSubscribeContentUseCase.executeV1(subscribeId, userDetails.getUser(), cursorRequest))
        );
    }
}