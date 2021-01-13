package com.example.aesparticipantes.Seguridad;

import org.joda.time.DateTime;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;


public class UserData extends AbstractAuthenticationToken {

    private static final long serialVersionUID = -1949976839306453197L;
    private String authenticatedUser;
    private String wcaNombre;
    private String wcaTemporalToken;
    private DateTime expirationTime;

    public UserData(String participante, String wcaNombre, String wcaTemporalTokenm, DateTime expirationTime){
        super(Collections.emptyList());
        this.authenticatedUser = participante;
        this.wcaNombre = wcaNombre;
        this.wcaTemporalToken = wcaTemporalTokenm;
        this.expirationTime = expirationTime;
    }

    @Override
    public String getCredentials() {
       if(expirationTime.isAfter(DateTime.now())){
           return wcaTemporalToken;
       } else {
           return null;
       }
    }

    @Override
    public String getPrincipal() {
        return authenticatedUser;
    }

    public String getWcaName(){
        return this.wcaNombre;
    }

}
