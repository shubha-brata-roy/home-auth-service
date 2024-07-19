package com.royhome.homeauthservice.security.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.royhome.homeauthservice.config.JWTConfiguration;
import com.royhome.homeauthservice.dtos.UserDto;
import com.royhome.homeauthservice.security.JWTDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component("JWTDecoderWithNimbusds")
public class JWTDecoderWithNimbusds implements JWTDecoder {
    private final JWTConfiguration jwtConfig;

    @Autowired
    public JWTDecoderWithNimbusds(JWTConfiguration jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public UserDto decode(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

        if(!signedJWT.verify(new MACVerifier(jwtConfig.getSecret().getBytes()))) {
            throw new JOSEException("Invalid token. Please sign in again.");
        }

        UserDto userDto = new UserDto();

        userDto.setEmail(claimsSet.getSubject());
        userDto.setName(claimsSet.getStringClaim("name"));
        userDto.setRoles(claimsSet.getStringListClaim("roles"));
        userDto.setExpiryAt(claimsSet.getExpirationTime());

        return userDto;
    }
}
