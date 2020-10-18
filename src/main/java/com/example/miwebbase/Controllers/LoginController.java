package com.example.miwebbase.Controllers;

import com.example.miwebbase.Entities.Categoria;
import com.example.miwebbase.repositories.CategoriaRepository;
import com.example.miwebbase.repositories.ParticipanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class LoginController {


    @Autowired
    ParticipanteRepository participanteRepository;

    @Autowired
    CategoriaRepository categoriaRepository;

    @Autowired
    LoginController self;


    @GetMapping("/")
    public String showForm(Model model) {

        model.addAttribute("participantes", self.getParticipantes());
        model.addAttribute("categorias", self.getCategoriasEnOrden());
        model.addAttribute("numJornadas", 5); //TODO CON REDISEÑO DE INICIO

        return "login";
    }

    @Cacheable("participantes")
    public List<String> getParticipantes() {
        return participanteRepository.getAllNames();
    }

    @Cacheable("listaDeCategorias")
    public List<Categoria> getCategoriasEnOrden() {
        return categoriaRepository.findAllByOrderByOrden();
    }



}
