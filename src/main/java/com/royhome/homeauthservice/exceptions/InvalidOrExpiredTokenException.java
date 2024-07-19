package com.royhome.homeauthservice.exceptions;

public class InvalidOrExpiredTokenException extends Exception {
    public InvalidOrExpiredTokenException(String message) {
        super(message);
    }
}
