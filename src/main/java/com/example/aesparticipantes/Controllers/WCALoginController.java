package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.Participante;
import com.example.aesparticipantes.Repositories.ParticipanteRepository;
import com.example.aesparticipantes.Seguridad.AuthProvider;
import com.example.aesparticipantes.Seguridad.WCAGetResponse;
import com.example.aesparticipantes.Seguridad.WCALoginResponse;
import com.example.aesparticipantes.Utils.AESUtils;
import com.example.aesparticipantes.Utils.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


@Controller
public class WCALoginController {

    @Autowired
    ParticipanteRepository participanteRepository;

    @Value("${wca.callbackUrlLogin}")
    private String callbackUrlLogin;

    @Autowired
    AuthProvider authenticationManager;

    @Autowired
    CacheManager cacheManager;

    Logger logger = LoggerFactory.getLogger(WCALoginController.class);

    @GetMapping("/loginInicio")
    public String callbackController(@RequestParam("code") String code, @RequestParam(value = "token", required = false) String token, @RequestParam(value = "expiresIn", required = false) Integer expiresIn, HttpServletRequest httpServletRequest, Model model) {
        try {

            //Si el token no es nulo es porque esto es un registro, llamado desde la plantilla mensaje TODO: hacerlo en otro endpoint, por ser ordenados
            if(token != null){

                WCAGetResponse perfilWCA = AuthUtils.getWCAUser(token);

                Participante participante = new Participante();
                participante.setNombre(perfilWCA.getMe().getName());
                participante.setConfirmado(true);
                participante.setWCAData(perfilWCA);
                participanteRepository.save(participante);

                AuthUtils.crearSesion(participante, token, expiresIn, perfilWCA, httpServletRequest, authenticationManager);
                model.addAttribute("mensaje", "Login correcto, redirigiendo");
                model.addAttribute("redirect", "/participante/" + participante.getNombre());

                logger.info("Registro de nuevo participante: "+ perfilWCA.getMe().getName());
                cacheManager.getCache("nombresDeParticipantes").clear();

                return "mensaje";

            }

            //TODO: handle refresh dentro del método get token, throw con 400
            WCALoginResponse wcaLoginResponse = AuthUtils.getWCAToken(code, callbackUrlLogin);
            WCAGetResponse perfilWCA = AuthUtils.getWCAUser(wcaLoginResponse.getAccess_token());
            Participante participante;
            if (perfilWCA.getMe().getWca_id() != null) {
                participante = participanteRepository.findByWcaId(perfilWCA.getMe().getWca_id());
                if (participante == null) { //Puede ser que se creara el usuario con wca id null y luego obtuviera id, entonces estará guardado sin id pero el nombre debería matchear
                    logger.info("Usuario no encontrado por id: "+ perfilWCA.getMe().getName());
                    participante = participanteRepository.findByNombre(perfilWCA.getMe().getName());
                }
            } else {
                participante = participanteRepository.findByNombre(perfilWCA.getMe().getName()); //Si no tiene WCA ID, es un usuario que se registró con el nombre de usuario de la WCA desde /registro
            }


            if (participante != null) {
                AuthUtils.crearSesion(participante, wcaLoginResponse.getAccess_token(), wcaLoginResponse.getExpires_in(), perfilWCA, httpServletRequest, authenticationManager);
                //TODO: If vinculado, actualizar base de datos (en segundo plano a ser posible)
                model.addAttribute("mensaje", "Login correcto, redirigiendo");
                model.addAttribute("redirect", "/participante/" + participante.getNombre());

                logger.info("Inicio de sesión: "+ perfilWCA.getMe().getName());
                return "mensaje";

            }


            model.addAttribute("mensaje", "Login correcto, pero no sabemos quién eres. Si ya has competido en otro campeonato online, ¡busca tu perfil y vincúlalo! Si es tu primera competición en AES Online, haz click aquí:");
            model.addAttribute("token", wcaLoginResponse.getAccess_token());
            model.addAttribute("expiresIn", wcaLoginResponse.getExpires_in());

            logger.info("No sabemos quién eres: "+ perfilWCA.getMe().getName());
            return "mensaje";


        } catch (Exception e) {
            model.addAttribute("mensaje", AESUtils.MENSAJE_ERROR + e.toString());
            logger.error("Error en inicio de sesión: "+ e.toString());
            return "mensaje";
        }

    }


}
