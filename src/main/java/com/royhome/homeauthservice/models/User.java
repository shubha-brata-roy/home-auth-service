package com.royhome.homeauthservice.models;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class User extends BaseModel {

    private String name;
    private String email;
    private String hashPassword;
    private String phoneNumber;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Role> roles;

    private boolean emailVerified = false;
}
