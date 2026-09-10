package com.epam.rd.autocode.assessment.appliances.dto;

public record AuthResponseDto(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        UserInfoDto user
) {
    public AuthResponseDto(String accessToken, String refreshToken, Long expiresIn, UserInfoDto user) {
        this(accessToken, refreshToken, "Bearer", expiresIn, user);
    }
}