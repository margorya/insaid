package org.task.security;

import io.jsonwebtoken.Jwts;
import lombok.SneakyThrows;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Instant;
import java.util.Date;

public class JwtTokenUtil {
    private static final KeyPair KEY_PAIR = generateKeyPair();

    public static String generateTokenByName(String name) {
        return Jwts.builder()
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(36000000)))
                .setSubject(name)
                .signWith(KEY_PAIR.getPrivate())
                .compact();
    }

    public static String getNameByToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY_PAIR.getPublic())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @SneakyThrows
    private static KeyPair generateKeyPair() {
        return KeyPairGenerator.getInstance("RSA").generateKeyPair();
    }
}
