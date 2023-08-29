package com.ppap.ppap.application.controller;

import com.ppap.ppap._core.security.CustomUserDetails;
import com.ppap.ppap._core.utils.ApiUtils;
import com.ppap.ppap.application.usecase.GetScrapSubscirbeUseCase;
import com.ppap.ppap.domain.scrap.service.ScrapReadService;
import com.ppap.ppap.domain.scrap.service.ScrapWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/scrap")
public class ScrapController {
    private final ScrapReadService scrapReadService;
    private final ScrapWriteService scrapWriteService;
    private final GetScrapSubscirbeUseCase getScrapSubscirbeUseCase;

    @PostMapping("/create/{content_id}")
    public ResponseEntity<?> create(@AuthenticationPrincipal CustomUserDetails userDetails,
                                    @PathVariable("content_id") Long contentId) {
        scrapWriteService.create(userDetails.getUser(), contentId);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @PostMapping("/delete/{scrap_id}")
    public ResponseEntity<?> deleteByScrapId(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable("scrap_id") Long scrapId) {
        scrapWriteService.deleteByScrapId(userDetails.getUser(), scrapId);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @PostMapping("/delete/content/{content_id}")
    public ResponseEntity<?> deleteByContentId(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable("content_id") Long contentId) {
        scrapWriteService.deleteByContentId(userDetails.getUser(), contentId);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

//    @GetMapping("/")
//    public ResponseEntity<?> getScrapList(@AuthenticationPrincipal CustomUserDetails userDetails) {
//
//    }
}
