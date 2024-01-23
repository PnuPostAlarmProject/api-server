package com.ppap.ppap._core.crawler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MechanicNoticeEnum {
	HAKBU("sub01_01.asp", "hakbunotice"),
	GRADE("sub01_02.asp", "gradnotice"),
	SUPERVISION("sub01_05.asp", "supervision");

	private final String suffix;
	private final String dbType;
}
