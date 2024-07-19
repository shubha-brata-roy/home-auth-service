package com.royhome.homeauthservice.security;

import com.nimbusds.jose.JOSEException;
import com.royhome.homeauthservice.dtos.UserDto;

import java.text.ParseException;

public interface JWTDecoder {
    public UserDto decode(String token) throws ParseException, JOSEException;
}
