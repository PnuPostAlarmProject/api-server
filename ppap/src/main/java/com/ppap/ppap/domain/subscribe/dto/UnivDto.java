package com.ppap.ppap.domain.subscribe.dto;

import java.util.Objects;

public record UnivDto(
	String college,
	String department
) {
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UnivDto univDto = (UnivDto)o;
		return Objects.equals(college, univDto.college) && Objects.equals(department,
			univDto.department);
	}

	@Override
	public int hashCode() {
		return Objects.hash(college, department);
	}
}
