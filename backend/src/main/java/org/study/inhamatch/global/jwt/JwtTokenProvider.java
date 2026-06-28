package org.study.inhamatch.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtProperties jwtProperties;
    private SecretKey getSigningKey() {
            return Keys.hmacShaKeyFor(
                    jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
            );
        }
        public String createToken(String email) {
            Date now = new Date();
            Date expiry = new Date(now.getTime() + jwtProperties.getAccessTokenValidityMs());
            return Jwts.builder()
                    .subject(email)
                    .issuedAt(now)
                    .expiration(expiry)
                    .signWith(getSigningKey())
                    .compact();
        }
        public boolean validateToken(String token){
            try{
                Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token);
                return true;
            } catch (Exception e) {
                return false;
            }
    }
    public Authentication getAuthentication(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String email = claims.getSubject();
        return new UsernamePasswordAuthenticationToken(
                email,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
