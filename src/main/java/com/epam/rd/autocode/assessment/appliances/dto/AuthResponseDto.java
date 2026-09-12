package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AuthResponseDto(

        @NotBlank(message = "{auth.accessToken.notBlank}")
        String accessToken,

        @NotBlank(message = "{auth.refreshToken.notBlank}")
        String refreshToken,

        @NotBlank(message = "{auth.tokenType.notBlank}")
        String tokenType,

        @NotNull(message = "{auth.expiresIn.notNull}")
        @Positive(message = "{auth.expiresIn.positive}")
        Long expiresIn,

        @NotNull(message = "{auth.user.notNull}")
        @Valid
        UserInfoDto user
) {
    public AuthResponseDto(String accessToken, String refreshToken, Long expiresIn, UserInfoDto user) {
        this(accessToken, refreshToken, "Bearer", expiresIn, user);
    }
}