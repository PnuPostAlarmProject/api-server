package com.ppap.ppap.domain.subscribe.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SubscribeChangePriorityDto(
	@NotEmpty(message = "데이터를 입력해주세요.")
	List<@NotNull(message = "Null을 입력해선 안됩니다.") Long> subscribeIds
) {
}
