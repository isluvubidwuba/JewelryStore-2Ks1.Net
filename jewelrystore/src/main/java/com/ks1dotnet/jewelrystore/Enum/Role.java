package com.ks1dotnet.jewelrystore.Enum;

public enum Role {
    ADMIN, MANAGER, STAFF, UNKNOW;

    public static Role fromString(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return UNKNOW;
        }
    }
}
