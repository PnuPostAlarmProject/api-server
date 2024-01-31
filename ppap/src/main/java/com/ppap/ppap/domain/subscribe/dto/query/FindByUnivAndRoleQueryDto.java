package com.ppap.ppap.domain.subscribe.dto.query;

public record FindByUnivAndRoleQueryDto(
	Long subscribeId,
	String title,
	String link
) {
	public FindByUnivAndRoleQueryDto {
	}
}
