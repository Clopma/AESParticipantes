package com.example.aesparticipantes.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;


@Controller
public class ScramblerController {


    @GetMapping("/scrambler")
    public String scrambler() throws IOException {

        return "Scrambler";
    }
}
