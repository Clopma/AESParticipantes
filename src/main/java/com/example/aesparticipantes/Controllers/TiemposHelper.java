package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
