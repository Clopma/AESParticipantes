package com.example.aesparticipantes.Seguridad;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class WCALoginResponse implements Serializable {
    public String access_token;
    public String token_type;
    public int expires_in;
    public String refresh_token;
    public String scope;
    public int created_at;
    public String error;
    public String error_description;
}
