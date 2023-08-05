package com.ppap.ppap._core.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ppap.ppap.domain.user.Entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {
    public static final Long EXP = 1000L * 60 * 30; // 30분
    public static final Long REFRESH_EXP = 1000L * 3600 * 24 * 7; // 1주일
    //    public static final Long EXP = 1000L; // 1초
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String REFRESH_TOKEN_PREFIX = "Refresh ";
    public static final String HEADER = "Authorization";

    public static String ACCESS_SECRET;
    public static String REFRESH_SECRET;

    @Value("${access-jwt-secret-key}")
    public void setACCESS_SECRET(String value) {
        ACCESS_SECRET = value;
    }

    @Value("${refresh-jwt-secret-key}")
    public void setRefresh_Secret(String value) { REFRESH_SECRET = value; }

    public static String create(User user) {

        String jwt = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP))
                .withClaim("id", user.getId())
                .withClaim("role", user.getRole().getRoleName())
                .sign(Algorithm.HMAC512(ACCESS_SECRET));
        return TOKEN_PREFIX + jwt;
    }

    public static String createRefreshToken(User user) {
        String jwt = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_EXP))
                .withClaim("id", user.getId())
                .withClaim("role", user.getRole().getRoleName())
                .sign(Algorithm.HMAC512(REFRESH_SECRET));
        return REFRESH_TOKEN_PREFIX + jwt;
    }

    public static DecodedJWT verify(String jwt) throws SignatureVerificationException, TokenExpiredException {
        jwt = jwt.replace(TOKEN_PREFIX, "");
        return JWT.require(Algorithm.HMAC512(ACCESS_SECRET))
                .build().verify(jwt);
    }

    public static DecodedJWT verifyRefreshToken(String jwt) throws SignatureVerificationException, TokenExpiredException {
        jwt = jwt.replace(REFRESH_TOKEN_PREFIX, "");
        return JWT.require(Algorithm.HMAC512(REFRESH_SECRET))
                .build().verify(jwt);
    }

    public static Long getRemainExpiration(String jwt) {
        DecodedJWT decodedJTW = verify(jwt);
        Date now = new Date();
        Date end = decodedJTW.getExpiresAt();
        return end.getTime() - now.getTime();
    }
}
