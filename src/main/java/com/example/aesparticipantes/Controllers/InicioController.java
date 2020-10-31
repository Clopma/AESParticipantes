package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.Categoria;
import com.example.aesparticipantes.Utils.AESUtils;
import com.example.aesparticipantes.repositories.CategoriaRepository;
import com.example.aesparticipantes.repositories.ParticipanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class InicioController {


    @Autowired
    ParticipanteRepository participanteRepository;

    @Autowired
    CategoriaRepository categoriaRepository;

    @Autowired
    InicioController self;


    @GetMapping("/")
    public String showForm(@CookieValue(value = AESUtils.COOKIE_TIPO_USUARIO, required = false) String tipoUsuario,
                           @CookieValue(value = AESUtils.COOKIE_NOMBRE_PARTICIPANTE, required = false) String nombreParticipante,
                           @CookieValue(value = AESUtils.COOKIE_WCA_ID, required = false) String wcaId,
                           Model model, HttpServletResponse httpServletResponse) {

        model.addAttribute("participantes", self.getParticipantes()); //TODO: Ponerte a ti mismo primero quizás...
        model.addAttribute("categorias", self.getCategoriasEnOrden());
        model.addAttribute("numJornadas", 5); //TODO CON REDISEÑO DE INICIO

        return "inicio";
    }

    @Cacheable("nombresDeParticipantes")
    public List<String> getParticipantes() {
        return participanteRepository.getNombres();
    }

    @Cacheable("listaDeCategorias")
    public List<Categoria> getCategoriasEnOrden() {
        return categoriaRepository.findAllByOrderByOrden();
    }


}
