package com.royhome.homeauthservice.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Session extends BaseModel {

    @Lob
    private String token;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private SessionStatus sessionStatus;
    private Date expiryAt;
}
