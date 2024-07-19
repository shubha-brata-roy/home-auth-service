package com.royhome.homeauthservice.security;

import com.nimbusds.jose.JOSEException;
import com.royhome.homeauthservice.dtos.JWTDto;

import java.util.List;

public interface JWTGenerator {
    public JWTDto generate(String email, String name, List<String> roles)
            throws JOSEException;
}
