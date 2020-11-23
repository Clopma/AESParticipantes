package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.Participante;
import com.example.aesparticipantes.Repositories.ParticipanteRepository;
import com.example.aesparticipantes.Seguridad.AuthProvider;
import com.example.aesparticipantes.Seguridad.WCAGetResponse;
import com.example.aesparticipantes.Seguridad.WCALoginResponse;
import com.example.aesparticipantes.Utils.AESUtils;
import com.example.aesparticipantes.Utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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


        @GetMapping("/loginInicio")
        public String callbackController(@RequestParam("code") String code, HttpServletRequest httpServletRequest, Model model) {

            try {

                WCALoginResponse wcaLoginResponse = AuthUtils.getWCAToken(code, callbackUrlLogin);
                WCAGetResponse perfilWCA = AuthUtils.getWCAUser(wcaLoginResponse.getAccess_token());
                Participante participante = participanteRepository.findByWcaId(perfilWCA.getMe().getWca_id());
                AuthUtils.crearSesion(participante, wcaLoginResponse, perfilWCA, httpServletRequest, authenticationManager);

                if (participante != null) { //TODO: Comprobar qué pasa cuando no está en la base de datos, debería devolver nulo

                    //TODO: If vinculado, actualizar base de datos (en segundo plano a ser posible)
                    model.addAttribute("mensaje", "Login correcto, redirigiendo");
                    model.addAttribute("redirect", "/participante/"+participante.getNombre());
                    return "mensaje";

                } else {

                    model.addAttribute("mensaje", "Login correcto, pero no sabemos quién eres. Si ya has competido en otro campeonato online, ¡busca tu perfil y vincúlalo!");
                    return "mensaje";
                }


            } catch (Exception e) {
                model.addAttribute("mensaje", AESUtils.MENSAJE_ERROR + e.toString());
                return "mensaje";
            }

        }





    }
