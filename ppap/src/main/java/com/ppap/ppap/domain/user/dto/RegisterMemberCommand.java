package com.ppap.ppap.domain.user.dto;


import jakarta.validation.constraints.*;
import lombok.Builder;

public record RegisterMemberCommand(
        @NotNull(message = "이메일 값을 입력해주세요.")
        @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 입력해주세요.")
        String email,

        @NotEmpty
        @Size(min = 6, max = 20, message = "6자에서 20자 이내여야 합니다.")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
                message = "영어 소문자, 영어 대문자, 숫자, 공백을 제외한 특수문자가 포함되어야 합니다.")
        String password
) {
        @Builder
        public RegisterMemberCommand {
        }
}
