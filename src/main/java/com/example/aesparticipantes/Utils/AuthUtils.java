package com.example.aesparticipantes.Utils;

import com.example.aesparticipantes.Entities.Participante;
import com.example.aesparticipantes.Seguridad.WCAGetResponse;
import com.example.aesparticipantes.Seguridad.WCALoginResponse;
import com.example.aesparticipantes.repositories.ParticipanteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthUtils {

    private static String clientSecret;

    @Value("${wca.clientSecret}")
    public void setClientSecret(String clientSecret) {
        AuthUtils.clientSecret = clientSecret;
    }

    private static String clientId;
    @Value("${wca.clientId}")
    public void setClientId(String clientId) {
        AuthUtils.clientId = clientId;
    }

    public static ParticipanteRepository participanteRepository;
    @Autowired
    public void setParticipanteRepository(ParticipanteRepository participanteRepository) {
        AuthUtils.participanteRepository = participanteRepository;
    }

    public static WCALoginResponse getWCAToken(String code, String callbackUrl, HttpServletResponse httpServletResponse) throws JsonProcessingException {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.set("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", callbackUrl);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        BodyInserter<MultiValueMap<String, String>, ClientHttpRequest>
                inserter = BodyInserters.fromFormData(body);

        String response = WebClient.create("https://www.worldcubeassociation.org/oauth/token")
                .post()
                .body(inserter)
                .retrieve().bodyToMono(String.class).block();

        WCALoginResponse wcaLoginResponse = new ObjectMapper().readValue(response, WCALoginResponse.class);
        Cookie tokenCookie = new Cookie(AESUtils.COOKIE_TOKEN_TEMPORAL, wcaLoginResponse.getAccess_token());
        tokenCookie.setMaxAge(wcaLoginResponse.getExpires_in());
        httpServletResponse.addCookie(tokenCookie);

        return wcaLoginResponse;
    }

    public static WCAGetResponse getWCAUser(String token, HttpServletResponse httpServletResponse) throws JsonProcessingException {

        String getResponse = WebClient.create("https://www.worldcubeassociation.org/api/v0/me/")
                .get()
                .header("Authorization", "Bearer " + token)
                .retrieve().bodyToMono(String.class).block();


        return new ObjectMapper().readValue(getResponse, WCAGetResponse.class);

    }

    public static void refreshCookies(HttpServletResponse httpServletResponse, WCAGetResponse wcaGetResponse, Participante participante) {
        AESUtils.TiposUsuarios tipoUsuario = AESUtils.getTipoUsuario(participante);


        httpServletResponse.addCookie(AuthUtils.createCookie(AESUtils.COOKIE_TIPO_USUARIO, tipoUsuario.name()));

        if(tipoUsuario.equals(AESUtils.TiposUsuarios.V)){
            Cookie cookieNOPA = AuthUtils.createCookie(AESUtils.COOKIE_NOMBRE_PARTICIPANTE, AESUtils.encodeURL(participante.getNombre()));
            httpServletResponse.addCookie(cookieNOPA);
        } else if(tipoUsuario.equals(AESUtils.TiposUsuarios.NC)){
            httpServletResponse.addCookie(AuthUtils.createCookie(AESUtils.COOKIE_WCA_ID, AESUtils.encodeURL(wcaGetResponse.getMe().getName())));
            httpServletResponse.addCookie(AuthUtils.createCookie(AESUtils.COOKIE_NOMBRE_PARTICIPANTE, AESUtils.encodeURL(participante.getNombre())));
        } else if(tipoUsuario.equals(AESUtils.TiposUsuarios.NV)) {
            httpServletResponse.addCookie(AuthUtils.createCookie(AESUtils.COOKIE_WCA_ID, AESUtils.encodeURL(wcaGetResponse.getMe().getWca_id())));
            httpServletResponse.addCookie(AuthUtils.createCookie(AESUtils.COOKIE_NOMBRE_WCA, AESUtils.encodeURL(wcaGetResponse.getMe().getName())));
        }
    }

    public static Cookie createCookie(String nombre, String valor){
        Cookie cookie = new Cookie(nombre, valor);
        cookie.setPath("/");
        return cookie;
    }

}
