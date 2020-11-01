package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.Categoria;
import com.example.aesparticipantes.Entities.Participante;
import com.example.aesparticipantes.Repositories.CategoriaRepository;
import com.example.aesparticipantes.Repositories.ParticipanteRepository;
import com.example.aesparticipantes.Seguridad.UserData;
import com.example.aesparticipantes.Utils.AESUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
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
    public String showForm(Model model, Principal principal) {

        if(principal instanceof UserData){

            String  nombreParticipanteGuardado = ((UserData) principal).getPrincipal();
            Participante participante = participanteRepository.findByNombre(nombreParticipanteGuardado);
            model.addAttribute("nombreWca", ((UserData) principal).getWcaName());

            if(participante == null) {
                model.addAttribute("tipoUsuario", AESUtils.TiposUsuarios.NV.name());
            } else {

                model.addAttribute("nombreParticipante", nombreParticipanteGuardado);
                if (participante.isConfirmado()){
                    model.addAttribute("tipoUsuario", AESUtils.TiposUsuarios.C.name());
                } else {
                    model.addAttribute("tipoUsuario", AESUtils.TiposUsuarios.NC.name());
                }
            }
        } else {
            model.addAttribute("tipoUsuario", AESUtils.TiposUsuarios.N.name());

        }

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
