package com.ppap.ppap.application.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ppap.ppap._core.exception.Exception400;
import com.ppap.ppap._core.utils.ApiUtils;
import com.ppap.ppap.application.usecase.GetUnivSubscribeUseCase;
import com.ppap.ppap.domain.subscribe.dto.UnivGetResponseDto;
import com.ppap.ppap.domain.subscribe.service.UnivReadService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/univ")
public class UnivController {

	private final UnivReadService univReadService;
	private final GetUnivSubscribeUseCase getUnivSubscribeUseCase;

	@GetMapping("/list")
	public ResponseEntity<?> getUnivList() {
		List<UnivGetResponseDto> result = univReadService.getUnivList();
		return ResponseEntity.ok(ApiUtils.success(result));
	}

	@GetMapping("/{univ_id}/subscribe")
	public ResponseEntity<?> getSubscribeByUnivId(@PathVariable("univ_id") Long univId){
		return ResponseEntity.ok(ApiUtils.success(getUnivSubscribeUseCase.execute(univId)));
	}
}
