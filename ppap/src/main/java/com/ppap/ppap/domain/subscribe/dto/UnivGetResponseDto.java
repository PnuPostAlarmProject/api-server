package com.ppap.ppap.domain.subscribe.dto;

import java.util.List;


import com.ppap.ppap.domain.subscribe.entity.Univ;

import lombok.Builder;

public record UnivGetResponseDto(
	String univ,
	List<UnivDetailDto> departments
) {

	public record UnivDetailDto(
		Long univId,
		String name
	) {
		@Builder
		public UnivDetailDto {
		}

		public static UnivDetailDto of(Univ univ) {
			return UnivDetailDto.builder()
				.univId(univ.getId())
				.name(univ.getDepartment())
				.build();
		}
	}
}
