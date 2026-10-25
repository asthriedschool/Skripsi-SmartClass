package com.smartclass.backend.security;

import org.jspecify.annotations.Nullable;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


public class BearerAuthToken extends AbstractAuthenticationToken {

    public static final String TOKEN_TYPE = "Bearer";

    private final String email;
    private final String credentials; //token


    private BearerAuthToken(
            @Nullable Collection<? extends GrantedAuthority> authorities,
            String email,
            String credentials,
            boolean isAuthenticated
    ) {

        super(authorities);

        this.email = email;
        this.credentials = credentials;

        this.setAuthenticated(isAuthenticated);
    }


    //Unauthenticated method
    public static BearerAuthToken unauthenticated(String token) {

        return new BearerAuthToken(
                null,
                null,
                token,
                false
        );
    }


    //Authenticated method
    public static BearerAuthToken authenticated(
            String email,
            Collection<? extends GrantedAuthority> roles
    ) {

        return new BearerAuthToken(
                roles,
                email,
                null,
                true
        );
    }


    @Override
    public @Nullable Object getCredentials() {
        return credentials;
    }


    @Override
    public @Nullable Object getPrincipal() {
        return email;
    }
}