package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.Participante;
import com.example.aesparticipantes.Models.TimelinePointDivisiones;
import com.example.aesparticipantes.Repositories.CategoriaRepository;
import com.example.aesparticipantes.Repositories.EventoRepository;
import com.example.aesparticipantes.Repositories.ParticipanteRepository;
import com.example.aesparticipantes.Repositories.TemporadaRepository;
import com.example.aesparticipantes.Seguridad.UserData;
import com.example.aesparticipantes.Utils.AESUtils;
import com.example.aesparticipantes.Utils.SpecialCaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class InicioController {


    @Autowired
    ParticipanteRepository participanteRepository;

    @Autowired
    CategoriaRepository categoriaRepository;

    @Autowired
    RankingGeneralController rankingGeneralController;

    @Autowired
    InicioController self;

    @Autowired
    EventoRepository eventoRepository;

    @Autowired
    TemporadaRepository temporadaRepository;

    @GetMapping("/")
    public String inicio(Model model, Principal principal) {


        if (principal instanceof UserData) {

            String nombreParticipanteGuardado = ((UserData) principal).getPrincipal();
            Optional<Participante> participante = participanteRepository.findByNombre(nombreParticipanteGuardado);
            model.addAttribute("nombreWca", ((UserData) principal).getWcaName());

            if (!participante.isPresent()) {
                model.addAttribute("tipoUsuario", AESUtils.TiposUsuarios.NV.name());
            } else {

                model.addAttribute("nombreParticipante", nombreParticipanteGuardado);
                if (participante.get().isConfirmado()) {
                    model.addAttribute("tipoUsuario", AESUtils.TiposUsuarios.C.name());
                } else {
                    model.addAttribute("tipoUsuario", AESUtils.TiposUsuarios.NC.name());
                }
            }
        } else {
            model.addAttribute("tipoUsuario", AESUtils.TiposUsuarios.N.name());
        }

        try {
            TimelinePointDivisiones timeline;
            timeline = TimelinePointDivisiones.getUltimaAcabada(temporadaRepository.findByNombre("Verano 2021").get());//TODO: Hacer, cachear, usar modelos y no entidades...
            model.addAttribute("temporadaActual", timeline.getTemporada());
            model.addAttribute("jornadaActual", timeline.getJornada());
        } catch (SpecialCaseException e) {
           // Los parámetros del model serán null y se tratarán desde el template
        }

        model.addAttribute("participantes", self.getParticipantes()); //TODO: Ponerte a ti mismo primero quizás...
        model.addAttribute("categorias", categoriaRepository.findAllByOrderByOrden());
        model.addAttribute("numJornadas", 5);

        return "inicio";
    }

    @Cacheable("nombresDeParticipantes")
    public List<String> getParticipantes() {
        return participanteRepository.getNombres();
    }


    @GetMapping("/cambios")
    public String verCambios() {
        return "cambios";
    }


}
