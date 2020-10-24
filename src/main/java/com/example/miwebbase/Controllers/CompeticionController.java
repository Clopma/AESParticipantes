package com.example.miwebbase.Controllers;

import com.example.miwebbase.Entities.Competicion;
import com.example.miwebbase.repositories.CompeticionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CompeticionController {

    @Autowired
    CompeticionRepository competicionRepository;


    @GetMapping("/competicion/{nombreCompeticion}")
    public String showForm(Model model, @PathVariable("nombreCompeticion") String nombreCompeticion) {
        Competicion competicion = competicionRepository.findByNombre(nombreCompeticion);
        return "competicion";
    }

}
