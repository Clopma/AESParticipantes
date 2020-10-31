package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.Participante;
import com.example.aesparticipantes.Seguridad.WCAGetResponse;
import com.example.aesparticipantes.Seguridad.WCALoginResponse;
import com.example.aesparticipantes.Utils.AESUtils;
import com.example.aesparticipantes.Utils.AuthUtils;
import com.example.aesparticipantes.repositories.ParticipanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;


@Controller
public class WCALoginController {

    @Autowired
    ParticipanteRepository participanteRepository;

    @Value("${wca.callbackUrl}")
    private String callbackUrl;


        @GetMapping("/loginWCA")
        public String callbackController(@RequestParam("code") String code, HttpServletResponse httpServletResponse, Model model) {

            try {

                WCALoginResponse wcaLoginResponse = AuthUtils.getWCAToken(code, callbackUrl, httpServletResponse);
                WCAGetResponse perfilWCA = AuthUtils.getWCAUser(wcaLoginResponse.getAccess_token(), httpServletResponse);
                Participante participante = participanteRepository.findByWcaId(perfilWCA.getMe().getWca_id());
                AuthUtils.refreshCookies(httpServletResponse, perfilWCA, participante);

                if (participante != null) {


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
