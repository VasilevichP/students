package com.example.demo.Controllers;

import com.example.demo.Services.LectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LectorController {
    @Autowired
    private LectorService lectorService;

    @GetMapping("/lector_menu")
    public String lector_main(Model model){

        return "lector_menu";
    }
}