package com.epam.parking.common;

public enum Roles {
    ROLE_ADMIN("ADMIN"),
    ROLE_USER("USER"),
    ROLE_SUPERVISOR("SUPERVISOR"),
    ROLE_MANAGER("MANAGER");

    String roleName;
    Roles(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return roleName;
    }
}
