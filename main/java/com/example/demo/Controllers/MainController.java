package com.example.demo.Controllers;

import com.example.demo.Entities.Faculties;
import com.example.demo.Services.SecretaryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
@Autowired
private SecretaryService secretaryService;

    @GetMapping("/")
    public String greeting(Model model, HttpSession session) {
        session.removeAttribute("user");
        session.removeAttribute("groups");
        session.removeAttribute("filter_faculty");
        session.removeAttribute("filter_specialty");
        session.removeAttribute("filter_course");
        session.removeAttribute("search");
        session.removeAttribute("groups");
        session.removeAttribute("gr_info");
        if(secretaryService.doesExist()) return "authorization";
        return "registration";
    }
}
