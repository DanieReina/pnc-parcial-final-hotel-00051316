package com.uca.pncparcialfinalhotel.repository;

import com.uca.pncparcialfinalhotel.entity.RefreshToken;
import com.uca.pncparcialfinalhotel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    // Útil para cuando el usuario cierra sesión o genera un nuevo refresh token
    void deleteByUser(User user);
}
