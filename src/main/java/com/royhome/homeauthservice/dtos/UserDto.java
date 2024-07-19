package com.royhome.homeauthservice.dtos;

import com.royhome.homeauthservice.models.Role;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserDto {
    private String email;
    private List<String> roles;
    private Date expiryAt;
    private String name;
}
