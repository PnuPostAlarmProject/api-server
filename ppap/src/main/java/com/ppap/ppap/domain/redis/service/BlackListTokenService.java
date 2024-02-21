package com.ppap.ppap.domain.redis.service;

import org.springframework.stereotype.Service;

import com.ppap.ppap._core.security.JwtProvider;
import com.ppap.ppap.domain.redis.entity.BlackListToken;
import com.ppap.ppap.domain.redis.repository.BlackListTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlackListTokenService {
	private final BlackListTokenRepository blackListTokenRepository;

	public void save(String accessToken) {
		Long remainTime = JwtProvider.getRemainExpiration(accessToken);
		blackListTokenRepository.save(
			BlackListToken.builder()
				.accessToken(accessToken)
				.expiration(remainTime)
				.build()
		);
	}

	public Boolean existByAccessToken(String accessToken) {
		return blackListTokenRepository.existsById(accessToken);
	}
}
