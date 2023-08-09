package com.ppap.ppap.application.controller;

import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception400;
import com.ppap.ppap._core.security.CustomUserDetails;
import com.ppap.ppap._core.security.JwtProvider;
import com.ppap.ppap._core.utils.ApiUtils;
import com.ppap.ppap.domain.user.dto.LoginMemberResponseDto;
import com.ppap.ppap.domain.user.dto.ReissueDto;
import com.ppap.ppap.domain.user.dto.oauth.kakao.KakaoUserInfo;
import com.ppap.ppap.domain.user.service.UserReadService;
import com.ppap.ppap.domain.user.service.UserWriteService;
import com.ppap.ppap.domain.user.utils.kakao.KakaoRestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Validated
public class UserController {

    private final UserReadService userReadService;
    private final UserWriteService userWriteService;
    private final KakaoRestTemplate kakaoRestTemplate;


    @PostMapping("/kakao/login")
    public ResponseEntity<?> kakaoLogin(HttpServletRequest request) {
        String token = request.getHeader("Kakao");
        if(token == null) {
            throw new Exception400(BaseExceptionStatus.KAKAO_TOKEN_MISSING);
        }

        KakaoUserInfo userInfo = kakaoRestTemplate.getKakaoUserInfo(token);
        LoginMemberResponseDto responseDto = userWriteService.socialLogin(userInfo);
        return ResponseEntity.ok(ApiUtils.success(responseDto));
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody @Valid ReissueDto.ReissueRequestDto reissueRequestDto) {
        ReissueDto.ReissueResponseDto responseDto = userReadService.reissue(reissueRequestDto);
        return ResponseEntity.ok(ApiUtils.success(responseDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = request.getHeader(JwtProvider.HEADER);
        userReadService.logout(token);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @GetMapping("/test")
    public String test(@AuthenticationPrincipal CustomUserDetails userDetails) {
        System.out.println(userDetails.getUser().getId());
        return "ok";
    }
}
