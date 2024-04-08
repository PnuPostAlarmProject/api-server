package com.ppap.ppap.application.controller;

import com.ppap.ppap._core.security.CustomUserDetails;
import com.ppap.ppap._core.utils.ApiUtils;
import com.ppap.ppap.application.usecase.GetScrapSubscirbeUseCase;
import com.ppap.ppap.domain.base.utils.CursorRequest;
import com.ppap.ppap.domain.scrap.dto.ScrapWithSubscribeDto;
import com.ppap.ppap.domain.scrap.service.ScrapReadService;
import com.ppap.ppap.domain.scrap.service.ScrapWriteService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/")
public class ScrapController {
    private final ScrapReadService scrapReadService;
    private final ScrapWriteService scrapWriteService;
    private final GetScrapSubscirbeUseCase getScrapSubscirbeUseCase;

    @PostMapping("v0/scrap/create/{content_id}")
    public ResponseEntity<?> create(@AuthenticationPrincipal CustomUserDetails userDetails,
                                    @PathVariable("content_id") Long contentId) {
        scrapWriteService.create(userDetails.getUser(), contentId);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    // 사용 안함
//    @PostMapping("/delete/{scrap_id}")
//    public ResponseEntity<?> deleteByScrapId(@AuthenticationPrincipal CustomUserDetails userDetails,
//                                             @PathVariable("scrap_id") Long scrapId) {
//        scrapWriteService.deleteByScrapId(userDetails.getUser(), scrapId);
//        return ResponseEntity.ok(ApiUtils.success(null));
//    }

    @PostMapping("v0/scrap/delete/content/{content_id}")
    public ResponseEntity<?> deleteByContentId(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable("content_id") Long contentId) {
        scrapWriteService.deleteByContentId(userDetails.getUser(), contentId);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @GetMapping({"v0/scrap", "v0/scrap/{subscribe_id}"})
    public ResponseEntity<?> getScrapList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable(required = false, name="subscribe_id") Optional<Long> subscribeId,
            @PageableDefault(size=10, page=0) Pageable pageable) {

        ScrapWithSubscribeDto responseDto = getScrapSubscirbeUseCase.execute(subscribeId, userDetails.getUser(), pageable);

        return ResponseEntity.ok(ApiUtils.success(responseDto));
    }

    @GetMapping({"v1/scrap", "v1/scrap/{subscribe_id}"})
    public ResponseEntity<?> getScrapListV1(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable(required = false, name="subscribe_id") Optional<Long> subscribeId,
        @RequestParam(value="cursor", required = false) LocalDateTime cursor,
        @RequestParam(value="pageSize", required = false, defaultValue = "10") int pageSize) {

        CursorRequest cursorRequest = new CursorRequest(cursor, pageSize);
        return ResponseEntity.ok(ApiUtils.success(
            getScrapSubscirbeUseCase.executeV1(subscribeId, userDetails.getUser(), cursorRequest)));
    }
}
