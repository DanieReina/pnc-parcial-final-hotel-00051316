package com.uca.pncparcialfinalhotel.controller;

import com.uca.pncparcialfinalhotel.dto.request.LoginRequestDTO;
import com.uca.pncparcialfinalhotel.dto.request.TokenRefreshRequestDTO;
import com.uca.pncparcialfinalhotel.dto.response.JwtResponseDTO;
import com.uca.pncparcialfinalhotel.entity.RefreshToken;
import com.uca.pncparcialfinalhotel.service.AuthService;
import com.uca.pncparcialfinalhotel.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        // Genera el JWT (15 minutos)
        String accessToken = authService.authenticateAndGenerateToken(request);
        // Genera el Refresh Token (7 días)
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.getUsername());

        return ResponseEntity.ok(new JwtResponseDTO(accessToken, refreshToken.getToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDTO> refreshToken(@Valid @RequestBody TokenRefreshRequestDTO request) {
        String requestRefreshToken = request.getRefreshToken();

        // Validamos el refresh token y generamos un nuevo access token si es válido
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = authService.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new JwtResponseDTO(newAccessToken, requestRefreshToken));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token inválido o expirado. Inicie sesión nuevamente."));
    }
}