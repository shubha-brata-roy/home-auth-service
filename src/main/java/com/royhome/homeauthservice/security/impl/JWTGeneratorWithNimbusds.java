package com.royhome.homeauthservice.security.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.royhome.homeauthservice.config.JWTConfiguration;
import com.royhome.homeauthservice.dtos.JWTDto;
import com.royhome.homeauthservice.security.JWTGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component("JWTGeneratorWithNimbusds")
public class JWTGeneratorWithNimbusds implements JWTGenerator {
    private final JWTConfiguration jwtConfig;
    private final JWSSigner signer;

    @Autowired
    public JWTGeneratorWithNimbusds(JWTConfiguration jwtConfig) {
        this.jwtConfig = jwtConfig;
        try {
            this.signer = new MACSigner(jwtConfig.getSecret().getBytes());
        } catch (KeyLengthException e) {
            throw new RuntimeException(e);
        }
    }

    public JWTDto generate(String email, String name, List<String> roles)
            throws JOSEException {
        Date expiryAt = new Date(new Date().getTime() + jwtConfig.getJwtExpirationInMs());

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(email)
                .expirationTime(expiryAt)
                .issueTime(new Date())
                .claim("name", name)
                .claim("roles", roles)
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);

        String token = signedJWT.serialize();

        JWTDto jwtDto = new JWTDto();
        jwtDto.setToken(token);
        jwtDto.setExpiryAt(expiryAt);

        return jwtDto;
    }
}
