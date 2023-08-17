package com.ppap.ppap.domain.user.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    ROLE_USER("ROLE_USER", "유저"),
    ROLE_ANONYMOUS("ROLE_ANONYMOUS", "익명"),
    ROLE_ADMIN("ROLE_ADMIN", "관리자");

    @Getter
    private final String roleName;

    @Getter
    private final String decription;
}
