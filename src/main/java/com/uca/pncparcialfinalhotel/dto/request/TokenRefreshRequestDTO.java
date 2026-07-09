package com.uca.pncparcialfinalhotel.dto.request;

import jakarta.validation.constraints.NotBlank;

public class TokenRefreshRequestDTO {

    @NotBlank(message = "El refresh token es obligatorio")
    private String refreshToken;

    // Getters y Setters
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
