package com.example.demo.Controllers;

import com.example.demo.Entities.Faculties;
import com.example.demo.Entities.Lector;
import com.example.demo.Entities.Secretary;
import com.example.demo.Entities.Student;
import com.example.demo.Repositories.SecretaryRepositary;
import com.example.demo.Services.AuthRegService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthRegController {
    @Autowired
    private AuthRegService authRegService;

    @GetMapping("/authorization")
    public String authorization(Model model, HttpSession session) {
        session.removeAttribute("user");
        session.removeAttribute("groups");
        session.removeAttribute("filter_faculty");
        session.removeAttribute("filter_specialty");
        session.removeAttribute("filter_course");
        session.removeAttribute("search");
        session.removeAttribute("groups");
        session.removeAttribute("gr_info");
        return "authorization";
    }

    @GetMapping("/registration")
    public String registration(Model model, HttpSession session) {
        session.removeAttribute("user");
        return "registration";
    }

    @PostMapping("/authorization/authorize")
    public String authorize(Model model, @RequestParam String login, @RequestParam String password, HttpSession httpSession) {
        model.addAttribute("login",login);
        model.addAttribute("password",password);
        Object o = authRegService.authorize(login, password);
        if (o instanceof Secretary) {
            httpSession.setAttribute("user",'s');
            return "redirect:/secretary_menu";
        }
        if (o instanceof Lector) {
            httpSession.setAttribute("user",'l');
            return "redirect:/lector_menu";
        }
        if (o instanceof Student) {
            httpSession.setAttribute("user",'t');
            return "redirect:/student_menu";
        }
        model.addAttribute("error", (String) o);
        return "authorization";
    }

    @PostMapping("/registration/register")
    public String register(Model model, @RequestParam String login, @RequestParam String password, @RequestParam int role,@RequestParam String email, RedirectAttributes redirectAttributes, HttpSession httpSession) {
        System.out.println("reg");
        model.addAttribute("login",login);
        model.addAttribute("password",password);
        model.addAttribute("role",role);
        if (!email.isEmpty()) model.addAttribute("email",email);
        switch (authRegService.register(login, password, role,email)) {
            case 2:
                System.out.println("2");
                model.addAttribute("error", "Неправильный номер ученического билета");
                break;
            case 4:
                System.out.println("4");
                model.addAttribute("error", "Неправильный ID");
                break;
            case 5:
                System.out.println("5");
                model.addAttribute("error", "Данный логин уже занят");
                break;
            case 8:
                System.out.println("8");
                model.addAttribute("emailerr", "Несоответствие адресов эл. почты");
                break;
            case 9:
                System.out.println("9");
                model.addAttribute("error", "Вы уже имеете свой личный кабинет");
                break;
            case 0:
                return "redirect:/authorization";
            default:
        }
        return "registration";
    }
}
