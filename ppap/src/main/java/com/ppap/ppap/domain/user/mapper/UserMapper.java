package com.ppap.ppap.domain.user.mapper;

import com.ppap.ppap.domain.user.entity.constant.Role;
import com.ppap.ppap.domain.user.entity.User;
import com.ppap.ppap.domain.user.dto.RegisterMemberCommand;
import com.ppap.ppap.domain.user.dto.RegisterMemberDto;
import com.ppap.ppap.domain.user.dto.oauth.OauthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public User registerMemberCommandToUser(RegisterMemberCommand registerMemberCommand) {

        return User.builder()
                .email(registerMemberCommand.email())
                .password(passwordEncoder.encode(registerMemberCommand.password()))
                .role(Role.ROLE_USER)
                .build();
    }

    public RegisterMemberDto userToRegisterMemberDto(User user) {
        return RegisterMemberDto.builder()
                .id(user.getId())
                .build();
    }

    public User userInfoToUser(OauthUserInfo userInfo) {
        return User.builder()
                .email(userInfo.email())
                .password(passwordEncoder.encode("{bcrypt}" + UUID.randomUUID()))
                .role(Role.ROLE_USER)
                .provider(userInfo.provider())
                .build();
    }
}
