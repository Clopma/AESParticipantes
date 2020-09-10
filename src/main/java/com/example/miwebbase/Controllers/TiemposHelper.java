package com.example.miwebbase.Controllers;

import com.example.miwebbase.repositories.CategoriaRepository;
import com.example.miwebbase.repositories.ParticipanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TiemposHelper {


    @Autowired
    CategoriaRepository categoriaRepository;


    @GetMapping("/tiempoHelper")
    public String show(Model model) {
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "tiempoHelper";
    }

}
