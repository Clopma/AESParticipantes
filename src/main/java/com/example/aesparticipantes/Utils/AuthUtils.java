package com.example.aesparticipantes.Utils;

import com.example.aesparticipantes.Entities.Participante;
import com.example.aesparticipantes.Repositories.ParticipanteRepository;
import com.example.aesparticipantes.Seguridad.AuthProvider;
import com.example.aesparticipantes.Seguridad.UserData;
import com.example.aesparticipantes.Seguridad.WCAGetResponse;
import com.example.aesparticipantes.Seguridad.WCALoginResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpServletRequest;

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

    public static WCALoginResponse getWCAToken(String code, String callbackUrl) throws JsonProcessingException {

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

        return new ObjectMapper().readValue(response, WCALoginResponse.class);
    }

    public static WCAGetResponse getWCAUser(String token) throws JsonProcessingException {

        String getResponse = WebClient.create("https://www.worldcubeassociation.org/api/v0/me/")
                .get()
                .header("Authorization", "Bearer " + token)
                .retrieve().bodyToMono(String.class).block();


        return new ObjectMapper().readValue(getResponse, WCAGetResponse.class);

    }


    public static void crearSesion(Participante participante, String token, int expiresIn, WCAGetResponse wcaGetResponse, HttpServletRequest request, AuthProvider authManager) {

        SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(new UserData(participante.getNombre(), wcaGetResponse.getMe().getName(), token, DateTime.now().plusSeconds(expiresIn))));

//        HttpSession session = request.getSession(true);
//        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);

    }

}
