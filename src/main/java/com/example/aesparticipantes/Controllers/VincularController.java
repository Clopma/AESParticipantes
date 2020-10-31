package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.Participante;
import com.example.aesparticipantes.Seguridad.WCAGetResponse;
import com.example.aesparticipantes.Utils.AESUtils;
import com.example.aesparticipantes.Utils.AuthUtils;
import com.example.aesparticipantes.repositories.ParticipanteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;


@Controller
public class VincularController {

    @Autowired
    ParticipanteRepository participanteRepository;

    @Value("${wca.clientId}")
    public String clientId;

    @Value("${wca.callbackUrlSoloValidar}")
    public String callbackUrlSoloValidar;

    @Autowired
    VincularController self;


    @RequestMapping("/vincular/{nombreParticipante}")
    public String vincularController(@CookieValue(value = AESUtils.COOKIE_TOKEN_TEMPORAL, required = false) String token,
                                     @PathVariable("nombreParticipante") String nombreParticipante,
                                     HttpServletResponse httpServletResponse, Model model) {

        Participante participante = participanteRepository.findByNombre(nombreParticipante);

        if (token == null) {
            model.addAttribute("mensaje", "Vinculación correcta, redirigiendo");
            model.addAttribute("redirect", "https://www.worldcubeassociation.org/oauth/authorize?client_id=" + clientId +
                    "&amp;redirect_uri=" + callbackUrlSoloValidar + "?participante=" + Base64.encode(nombreParticipante.getBytes()) + "&response_type=code&amp;scope=public");

            return "mensaje";
        } else {
            return self.vincular(participante, model, token, httpServletResponse);
        }


    }


    //TODO: Handle exceptions
    @RequestMapping("/validar")
    public String vincularController(@RequestParam("participante") String nombreParticipanteEncoded,
                                     @RequestParam("code") String code, Model model, HttpServletResponse httpServletResponse)
            throws Base64DecodingException, JsonProcessingException {

        String callbackUrl = callbackUrlSoloValidar + "?participante=" + nombreParticipanteEncoded;
        String nombreParticipante = new String(Base64.decode(nombreParticipanteEncoded.getBytes()));
        return self.vincular(participanteRepository.findByNombre(nombreParticipante), model, AuthUtils.getWCAToken(code, callbackUrl, httpServletResponse).getAccess_token(), httpServletResponse);


    }

    //Solo llamar desde bean
    @CacheEvict(value = "participantes", key = "#participante.nombre")
    public String vincular(Participante participante, Model model, String token, HttpServletResponse httpServletResponse) {

        if (participante.isConfirmado()) {
            // Alguien ha accedido por un medio que no es el botón
            model.addAttribute("mensaje", "No deberías haber encontrado esto. Por favor, usa el link de contacto de inicio y cuéntame cómo has llegado aquí.");
            return "mensaje";
        } else {
            try {
                WCAGetResponse perfilWCA = AuthUtils.getWCAUser(token, httpServletResponse);
                Participante participantePotencial = participanteRepository.findByWcaId(perfilWCA.getMe().getWca_id());

                if(participantePotencial == null || participantePotencial.equals(participante)){

                    participante.setWcaId(perfilWCA.getMe().getWca_id());
                    participante.setNombreWCA(perfilWCA.getMe().getName());
                    participante.setFechaActualicazionWCA(perfilWCA.getMe().getUpdated_at());
                    participante.setFechaCreacionWCA(perfilWCA.getMe().getCreated_at());
                    participante.setGender(perfilWCA.getMe().getGender());
                    participante.setLinkPerfilWCA(perfilWCA.getMe().getUrl());
                    participante.setPais(perfilWCA.getMe().getCountry_iso2());
                    participante.setUrlImagenPerfil(perfilWCA.getMe().getAvatar().getUrl());
                    participante.setUrlImagenPerfilIcono(perfilWCA.getMe().getAvatar().getThumb_url());
                    participante.setConfirmado(true);

                    participanteRepository.save(participante);
                    AuthUtils.refreshCookies(httpServletResponse, perfilWCA, participante);

                    model.addAttribute("mensaje", "Vinculación correcta, redirigiendo");
                    model.addAttribute("redirect", "/participante/" + participante.getNombre());

                    return "mensaje";
                } else {
                    model.addAttribute("mensaje", "Vinculación incorrecta: Hay otro usuario potencialmente vinculado a esta cuenta. " +
                            "Por favor, contacta con un administrador para aclarar la autoría del perfil.");
                    return "mensaje";

                }

            } catch (JsonProcessingException e) {
                model.addAttribute("mensaje", AESUtils.MENSAJE_ERROR + e.toString());
                return "mensaje";
            }


        }
    }

}
