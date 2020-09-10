package com.example.miwebbase.Controllers;

import com.example.miwebbase.repositories.ParticipanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {


    @Autowired
    ParticipanteRepository participanteRepository;

    @GetMapping("/")
    public String showForm(Model model) {

        model.addAttribute("participantes", participanteRepository.getAllNames());


        return "login";
    }

}
