package com.ppap.ppap.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ppap.ppap.domain.subscribe.dto.query.FindByUnivAndRoleQueryDto;
import com.ppap.ppap.domain.subscribe.service.SubscribeReadService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GetUnivSubscribeUseCase {
	private final SubscribeReadService subscribeReadService;

	public List<FindByUnivAndRoleQueryDto>  execute(Long univId) {
		return subscribeReadService.getSubscribeByUnivId(univId);
	}
}
