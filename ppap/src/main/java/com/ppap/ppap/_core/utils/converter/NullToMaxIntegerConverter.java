package com.ppap.ppap._core.utils.converter;

import java.util.Objects;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

//autoApply True시 모든 엔티티의 Integer필드에 대해서 자동 적용되므로 주의해야한다.
@Converter
public class NullToMaxIntegerConverter implements AttributeConverter<Integer, Integer> {
	@Override
	public Integer convertToDatabaseColumn(Integer attribute) {
		//특별한 변환 없이 그대로 값을 데이터베이스에 저장.
		return attribute;
	}

	@Override
	public Integer convertToEntityAttribute(Integer dbData) {
		// 데이터베이스에서 읽은 값이 null이면 Integer.MAX_VALUE로 변환
		if (Objects.isNull(dbData))
			return Integer.MAX_VALUE;

		return dbData;
	}
}
