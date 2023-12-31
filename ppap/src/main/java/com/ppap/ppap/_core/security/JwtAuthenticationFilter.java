package com.ppap.ppap._core.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ppap.ppap.domain.user.entity.constant.Role;
import com.ppap.ppap.domain.user.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String jwt = request.getHeader(JwtProvider.HEADER);

        if (jwt == null) {
            chain.doFilter(request, response);
            return;
        }

        if (!jwt.startsWith("Bearer ")) {
            log.error("잘못된 토큰");
            throw new JWTDecodeException("토큰 형식이 잘못되었습니다.");
        }

        try{
            DecodedJWT decodedJWT = JwtProvider.verify(jwt);
            Long id = decodedJWT.getClaim("id").asLong();
            String role = decodedJWT.getClaim("role").asString();
            User user = User.builder().id(id).role(Role.valueOf(role)).build();
            CustomUserDetails userDetails = new CustomUserDetails(user);
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            userDetails.getPassword(),
                            userDetails.getAuthorities()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("디버그 : 인증 객체 만들어짐");
        } catch (SignatureVerificationException sve) {
            log.error("토큰 검증 실패");
            throw new JWTVerificationException("토큰 검증이 실패했습니다.");
        } catch (TokenExpiredException tee) {
            log.error("토큰 만료 됨");
        } catch (JWTDecodeException jde) {
            log.error("잘못된 토큰");
            throw new JWTDecodeException("토큰 형식이 잘못되었습니다.");
        }

        chain.doFilter(request, response);
    }
}
