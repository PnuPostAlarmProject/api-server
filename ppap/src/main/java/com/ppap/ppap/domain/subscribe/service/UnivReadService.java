package com.ppap.ppap.domain.subscribe.service;

import static java.util.stream.Collectors.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ppap.ppap.domain.subscribe.dto.UnivGetResponseDto;
import com.ppap.ppap.domain.subscribe.entity.Univ;
import com.ppap.ppap.domain.subscribe.repository.UnivJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UnivReadService {
	private final UnivJpaRepository univJpaRepository;

	public List<UnivGetResponseDto> getUnivList() {
		List<Univ> univList = univJpaRepository.findAll();
		Map<String, List<UnivGetResponseDto.UnivDetailDto>> univDtoMap = univList.stream()
			.collect(groupingBy(
				Univ::getCollege,
				TreeMap::new,
				mapping(UnivGetResponseDto.UnivDetailDto::of, toList())
			));

		for (List<UnivGetResponseDto.UnivDetailDto> univDtoList: univDtoMap.values())
			univDtoList.sort(Comparator.comparing(UnivGetResponseDto.UnivDetailDto::name));

		return univDtoMap.entrySet().stream()
			.map(entry -> new UnivGetResponseDto(entry.getKey(), entry.getValue()))
			.toList();
	}
}
