package com.uca.pncparcialfinalhotel.service;

import com.uca.pncparcialfinalhotel.dto.request.LoginRequestDTO;
import com.uca.pncparcialfinalhotel.security.jwt.JwtProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public AuthService(AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    public String authenticateAndGenerateToken(LoginRequestDTO request) {
        // Delega la validación de la contraseña al AuthenticationManager de Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Si la contraseña es correcta, generamos el JWT de 15 minutos
        return jwtProvider.generateTokenFromUsername(request.getUsername());
    }

    public String generateTokenFromUsername(String username) {
        return jwtProvider.generateTokenFromUsername(username);
    }
}