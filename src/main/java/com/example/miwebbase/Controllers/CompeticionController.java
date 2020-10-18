package com.example.miwebbase.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CompeticionController {



    @GetMapping("/competicion/{nombreCompeticion}")
    public String showForm(Model model, @PathVariable("nombreCompeticion") String nombreCompeticion) {

        return "competicion";
    }

}
