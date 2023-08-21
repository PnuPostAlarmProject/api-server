package com.ppap.ppap.domain.user.service;

import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception400;
import com.ppap.ppap._core.exception.Exception500;
import com.ppap.ppap._core.security.JwtProvider;
import com.ppap.ppap.domain.redis.service.RefreshTokenService;
import com.ppap.ppap.domain.user.entity.User;
import com.ppap.ppap.domain.user.repository.UserJpaRepository;
import com.ppap.ppap.domain.user.dto.LoginMemberResponseDto;
import com.ppap.ppap.domain.user.dto.RegisterMemberCommand;
import com.ppap.ppap.domain.user.dto.RegisterMemberDto;
import com.ppap.ppap.domain.user.dto.oauth.OauthUserInfo;
import com.ppap.ppap.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserWriteService {
    private final UserMapper userMapper;
    private final UserJpaRepository userJpaRepository;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public LoginMemberResponseDto socialLogin(OauthUserInfo userInfo) {
        User user = userJpaRepository.findByEmail(userInfo.email()).orElseGet(
                () -> userJpaRepository.save(userMapper.userInfoToUser(userInfo)));

        String accessToken = JwtProvider.create(user);
        String refreshToken = JwtProvider.createRefreshToken(user);

        refreshTokenService.save(refreshToken, accessToken, user);
        return LoginMemberResponseDto.of(accessToken, refreshToken);
    }

    @Transactional
    public RegisterMemberDto create(RegisterMemberCommand registerMemberCommand) {

        // 이메일이 이미 존재하는지 검증
        checkEmail(registerMemberCommand.email());

        User user = userMapper.registerMemberCommandToUser(registerMemberCommand);
        try {
            userJpaRepository.save(user);
            return userMapper.userToRegisterMemberDto(user);
        } catch(Exception e) {
            // logging 추가
            e.printStackTrace();
            throw new Exception500(BaseExceptionStatus.USER_SAVE_ERROR);
        }
    }

    // 유저가 이미 존재한다면 예외처리
    private void checkEmail(String email){
        if(userJpaRepository.existsByEmail(email))
            throw new Exception400(BaseExceptionStatus.USER_ALREADY_EXIST);
    }
}
