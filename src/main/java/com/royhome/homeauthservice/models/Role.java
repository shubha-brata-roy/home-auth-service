package com.royhome.homeauthservice.models;

public enum Role {
    ADMIN,
    USER,
    VIEWER;

    public static boolean contains(String role) {
        for (Role r : Role.values()) {
            if (r.name().equals(role)) {
                return true;
            }
        }
        return false;
    }
}
