package com.yyggee.eggs.model.ds1;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_ADMIN,
    ROLE_CLIENT,
    ROLE_ACCOUNT_MANAGER;

    public String getAuthority() {
        return name();
    }

}
