package com.example.aesparticipantes.Seguridad;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;


@Component
public class AuthProvider implements AuthenticationProvider {

    @Override
    public UserData authenticate(Authentication authentication)
            throws AuthenticationException {
       return (UserData) authentication;

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UserData.class);
    }

}
