package com.ppap.ppap.domain.user.service;

import java.util.Optional;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception400;
import com.ppap.ppap._core.exception.Exception404;
import com.ppap.ppap._core.security.JwtProvider;
import com.ppap.ppap.domain.redis.service.RefreshTokenService;
import com.ppap.ppap.domain.user.entity.User;
import com.ppap.ppap.domain.user.entity.constant.Role;
import com.ppap.ppap.domain.user.repository.UserJpaRepository;
import com.ppap.ppap.domain.user.dto.ReissueDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserReadService {

    private final RefreshTokenService refreshTokenService;

    public ReissueDto.ReissueResponseDto reissue(ReissueDto.ReissueRequestDto requestDto) {
        String refreshToken = requestDto.refreshToken();

        User user = getUserByRefreshToken(refreshToken);

        checkRefreshTokenInRedis(refreshToken);

        String newAccessToken = JwtProvider.create(user);
        String newRefreshToken = JwtProvider.createRefreshToken(user);
        refreshTokenService.deleteById(refreshToken);
        refreshTokenService.save(newRefreshToken, newAccessToken, user);

        return ReissueDto.ReissueResponseDto.of(newAccessToken, newRefreshToken);
    }

    public void logout(String accessToken) {
        refreshTokenService.deleteByAccessToken(accessToken);
    }

    private void checkRefreshTokenInRedis(String refreshToken) {
        if(!refreshTokenService.existsById(refreshToken)) {
            throw new Exception404(BaseExceptionStatus.REFRESH_TOKEN_NOT_FOUND);
        }
    }

    private User getUserByRefreshToken(String refreshToken) {
        try{
            DecodedJWT decodedJWT = JwtProvider.verifyRefreshToken(refreshToken);
            String email = decodedJWT.getClaim("email").asString();
            Long id = decodedJWT.getClaim("id").asLong();
            Role role = Role.valueOf(decodedJWT.getClaim("role").asString());
            return User.builder().id(id).email(email).role(role).build();
        } catch (SignatureVerificationException | JWTDecodeException e) {
            throw new Exception400(BaseExceptionStatus.REFRESH_TOKEN_INVALID);
        } catch (TokenExpiredException tee) {
            throw new Exception400(BaseExceptionStatus.REFRESH_TOKEN_EXPIRED);
        }
    }
}
