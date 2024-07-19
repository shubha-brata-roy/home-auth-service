package com.royhome.homeauthservice.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class JWTDto {
    private String token;
    private Date expiryAt;
}
