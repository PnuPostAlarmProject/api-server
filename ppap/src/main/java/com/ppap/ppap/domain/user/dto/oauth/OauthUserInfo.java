package com.ppap.ppap.domain.user.dto.oauth;

import com.ppap.ppap.domain.user.Entity.constant.Provider;

public interface OauthUserInfo {

    String email();
    Provider provider();
    Long id();
}
