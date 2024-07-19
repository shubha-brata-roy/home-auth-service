package com.royhome.homeauthservice.security.impl;

import com.nimbusds.jose.JOSEException;
import com.royhome.homeauthservice.config.JWTConfiguration;
import com.royhome.homeauthservice.dtos.JWTDto;
import com.royhome.homeauthservice.security.JWTGenerator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Component("JWTGeneratorWithJJWT")
public class JWTGeneratorWithJJWT implements JWTGenerator {
    private final JWTConfiguration jwtConfig;

    @Autowired
    public JWTGeneratorWithJJWT(JWTConfiguration jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Override
    public JWTDto generate(String email, String name, List<String> roles) throws JOSEException {

        Date expiryAt = new Date(new Date().getTime() + jwtConfig.getJwtExpirationInMs());

        Map<String,Object> claims = new HashMap<>();
        claims.put("subject", email);
        claims.put("expirationTime", expiryAt);
        claims.put("issueTime", new Date());
        claims.put("name", name);
        claims.put("roles", roles);

        SecretKey secretKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());

        String token = Jwts.builder()
                .claims(claims)
                .signWith(secretKey)
                .compact();

        JWTDto jwtDto = new JWTDto();
        jwtDto.setToken(token);
        jwtDto.setExpiryAt(expiryAt);

        return jwtDto;
    }
}
