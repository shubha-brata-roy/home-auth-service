package com.royhome.homeauthservice.security.impl;

import com.nimbusds.jose.JOSEException;
import com.royhome.homeauthservice.config.JWTConfiguration;
import com.royhome.homeauthservice.dtos.UserDto;
import com.royhome.homeauthservice.security.JWTDecoder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.text.ParseException;
import java.util.List;

@Primary
@Component("JWTDecoderWithJJWT")
public class JWTDecoderWithJJWT implements JWTDecoder {
    private final JWTConfiguration jwtConfig;

    @Autowired
    public JWTDecoderWithJJWT(JWTConfiguration jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Override
    public UserDto decode(String token) throws ParseException, JOSEException {

        SecretKey secretKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        UserDto userDto = new UserDto();

        userDto.setEmail((String)claims.getSubject());
        userDto.setName((String)claims.get("name", String.class));
        userDto.setRoles(claims.get("roles", List.class));
        userDto.setExpiryAt(claims.getExpiration());

        return userDto;
    }
}
