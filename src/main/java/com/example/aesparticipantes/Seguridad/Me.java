package com.example.aesparticipantes.Seguridad;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Me {
    @JsonProperty("class")
    public String userClass;
    public String url;
    public int id;
    public String wca_id;
    public String name;
    public String gender;
    public String country_iso2;
    public Object delegate_status;
    public Date created_at;
//    public String email;
    public Date updated_at;
    public List<Object> teams;
    public Avatar avatar;
}
