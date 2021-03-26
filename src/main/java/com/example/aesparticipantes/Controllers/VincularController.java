package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.Participante;
import com.example.aesparticipantes.Repositories.ParticipanteRepository;
import com.example.aesparticipantes.Seguridad.AuthProvider;
import com.example.aesparticipantes.Seguridad.UserData;
import com.example.aesparticipantes.Seguridad.WCAGetResponse;
import com.example.aesparticipantes.Seguridad.WCALoginResponse;
import com.example.aesparticipantes.Utils.AESUtils;
import com.example.aesparticipantes.Utils.AuthUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Optional;


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

    @Autowired
    AuthProvider authManager;

    Logger logger = LoggerFactory.getLogger(VincularController.class);


    @RequestMapping("/vincular/{nombreParticipante}")
    public String vincularController(@PathVariable("nombreParticipante") String nombreParticipante,
                                     HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, Model model, Principal principal) {

        Optional<Participante> participante = participanteRepository.findByNombre(nombreParticipante);

        if(!participante.isPresent()){
            logger.error("404 al vincular: " + nombreParticipante);
            return "error/404";
        }

        if (principal instanceof UserData && ((UserData) principal).getCredentials() != null ) {
            return self.vincular(participante.get(), model, WCALoginResponse.builder().access_token(((UserData) principal).getCredentials()).build(), httpServletResponse, httpServletRequest);
        } else {

        model.addAttribute("mensaje", "Vinculando...");
        model.addAttribute("redirect", "https://www.worldcubeassociation.org/oauth/authorize?client_id=" + clientId +
                "&amp;redirect_uri=" + callbackUrlSoloValidar + "?participante=" + Base64.encode(nombreParticipante.getBytes()) + "&response_type=code&amp;scope=public");

        logger.info("Vinculando: "+ nombreParticipante);
        return "mensaje";

        }
    }


    //TODO: Handle exceptions
    @RequestMapping("/validar")
    public String vincularController(@RequestParam("participante") String nombreParticipanteEncoded,
                                     @RequestParam("code") String code, Model model, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest)
            throws Base64DecodingException, JsonProcessingException {

        String callbackUrl = callbackUrlSoloValidar + "?participante=" + nombreParticipanteEncoded;
        String nombreParticipante = new String(Base64.decode(nombreParticipanteEncoded.getBytes()));
        return self.vincular(participanteRepository.findByNombre(nombreParticipante).get(), model, AuthUtils.getWCAToken(code, callbackUrl), httpServletResponse, httpServletRequest);


    }

    //Solo llamar desde bean
    @CacheEvict(value = "participantes", key = "#participante.nombre")
    public String vincular(Participante participante, Model model, WCALoginResponse wcaLoginResponse, HttpServletResponse httpServletResponse, HttpServletRequest request) {

        if (participante.isConfirmado()) {
            // Alguien ha accedido por un medio que no es el botón
            logger.error("Intentando vincular participante condirmado: "+ participante.getNombre());
            model.addAttribute("mensaje", "No deberías haber encontrado esto. Por favor, usa el link de contacto de inicio y cuéntame cómo has llegado aquí.");
            return "mensaje";
        } else {
            try {
                WCAGetResponse perfilWCA = AuthUtils.getWCAUser(wcaLoginResponse.getAccess_token());
                Optional<Participante> participantePotencial = Optional.empty();

                if (perfilWCA.getMe().getWca_id() != null) {
                     participantePotencial = participanteRepository.findByWcaId(perfilWCA.getMe().getWca_id());
                }

                    if (!participantePotencial.isPresent() || participantePotencial.equals(participante)) {

                        participante.setWCAData(perfilWCA);
                        participante.setConfirmado(true);
                        participanteRepository.save(participante);

                        AuthUtils.crearSesion(participante, wcaLoginResponse.getAccess_token(), wcaLoginResponse.getExpires_in(), perfilWCA, request, authManager);

                        model.addAttribute("mensaje", "Vinculación correcta, redirigiendo");
                        model.addAttribute("redirect", "/participante/" + participante.getNombre());
                        logger.info("Participante vinculado: "+ participante.getNombre());
                        return "mensaje";
                    } else {
                        model.addAttribute("mensaje", "Vinculación incorrecta: Hay otro usuario potencialmente vinculado a esta cuenta. " +
                                "Por favor, contacta con un administrador para aclarar la autoría del perfil.");
                        logger.error("Vinculación incorrecta: Se ha intentado vincular a "+ participante.getNombre() + " pero no coincide con el potencial: "+ participantePotencial.get().getNombre());
                        return "mensaje";

                    }


            } catch (JsonProcessingException e) {
                model.addAttribute("mensaje", AESUtils.MENSAJE_ERROR + e.toString());
                logger.error("Error en vinculación con " + participante.getNombre() + ": "+ e.toString());
                return "mensaje";
            }


        }
    }

}
