package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.*;
import com.example.aesparticipantes.Models.Inscripcion;
import com.example.aesparticipantes.Repositories.CategoriaRepository;
import com.example.aesparticipantes.Repositories.CompeticionRepository;
import com.example.aesparticipantes.Repositories.TiempoRepository;
import com.example.aesparticipantes.Seguridad.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ParticiparController {

    @Autowired
    CompeticionRepository competicionRepository;

    @Autowired
    ParticipanteController participanteController;

    @Autowired
    TiempoRepository tiempoRepository;

    @Autowired
    CategoriaRepository categoriaRepository;

    @GetMapping("/participar/{nombreCompeticion}")
    public String elegirCategoria(@PathVariable("nombreCompeticion") String nombreCompeticion, Model model, Principal principal) {

        Participante participanteLogeado;
        if (principal instanceof UserData) {
            String nombreParticipanteGuardado = ((UserData) principal).getPrincipal();
             participanteLogeado = participanteController.getParticipante(nombreParticipanteGuardado);
            if (participanteLogeado == null){
                return noLogeado(model);
            }
        } else {
            return noLogeado(model);
        }

        Competicion competicion = competicionRepository.findByNombre(nombreCompeticion);
        if (competicion == null){
            return "error/404";
        }

        Optional<Jornada> jornadaActiva = competicion.getJornadaActiva();
        if (!jornadaActiva.isPresent()){
            model.addAttribute("mensaje", "No hay ninguna jornada activa en este campeonato. Si crees que esto es un error, contacta a un administrador.");
            return "mensaje";
        }


        List<Categoria> categoriasParticipadas = tiempoRepository.getTiemposEnJornada(jornadaActiva.get(), participanteLogeado).stream()
                .map(Tiempo::getCategoria).collect(Collectors.toList());

        List<Inscripcion> categoriasInscritas = participanteLogeado.getInscripcionesParticipadasYNoParticipadasEnCompeticion(competicion, categoriasParticipadas);



        model.addAttribute("inscripciones", categoriasInscritas);

        return "elegirCategoria";
    }

    private String noLogeado(Model model) {
        model.addAttribute("mensaje", "Necesitas estar logueado para participar.");
        return "mensaje";
    }

    @GetMapping("/participar/{nombreCompeticion}/{nombreCategoria}")
    public String evento(@PathVariable("nombreCategoria") String nombreCategoria, @PathVariable("nombreCompeticion") String nombreCompeticion, Model model, Principal principal) {


        //TODO: Código duplicado, meter en métodos de validación: con un throws custom exception con un campo para la template y otro para el mensaje
            Participante participanteLogeado;
            if (principal instanceof UserData) {
                String nombreParticipanteGuardado = ((UserData) principal).getPrincipal();
                participanteLogeado = participanteController.getParticipante(nombreParticipanteGuardado);
                if (participanteLogeado == null) {
                    return noLogeado(model);
                }
            } else {
                return noLogeado(model);
            }

            Competicion competicion = competicionRepository.findByNombre(nombreCompeticion);
            if (competicion == null) {
                return "error/404";
            }

            Optional<Jornada> jornadaActiva = competicion.getJornadaActiva();
            if (!jornadaActiva.isPresent()) {
                model.addAttribute("mensaje", "No hay ninguna jornada activa en este campeonato. Si crees que esto es un error, contacta a un administrador.");
                return "mensaje";
            }
         // FIN TODO

        Categoria categoria = categoriaRepository.findByNombre(nombreCategoria); //TODO: no lo cacheo porque quiero comprobar si se queda en memoria y no hace la query
        List<Inscripcion> categoriasInscritas = participanteLogeado.getInscripcionesParticipadasYNoParticipadasEnCompeticion(competicion);

        Optional<Inscripcion> posibleInscripcion = categoriasInscritas.stream().filter(i -> i.getCategoria().equals(categoria)).findAny();
        if (!posibleInscripcion.isPresent()){
            model.addAttribute("mensaje", "No estás inscrito en esa categoría. Por favor, no intentes hacer trampas. Si crees que esto es un error, contacta a un administrador.");
            return "mensaje";
        }

        Inscripcion inscripcion = posibleInscripcion.get();

        if (inscripcion.isParticipado()){
            model.addAttribute("mensaje", "Ya has participado en este. Por favor, no intentes hacer trampas. Si crees que esto es un error, contacta a un administrador.");
            return "mensaje";
        }





        return "mensaje";
    }

}
