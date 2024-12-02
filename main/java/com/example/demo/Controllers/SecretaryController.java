package com.example.demo.Controllers;

import com.example.demo.Entities.*;
import com.example.demo.Services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class SecretaryController {
    @Autowired
    private SecretaryService secretaryService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/secretary_menu")
    public String secretary_menu(Model model, HttpSession httpSession) {
        Character user = (Character) httpSession.getAttribute("user");
        if (user != null) {
            if (user == 's') {
                List<Omission> omissions = scheduleService.getOmissionsByStatus(0);
                List<Student> students = new ArrayList<>();
                for (Omission o:omissions){
                    students.add(studentService.getById(o.getStudent()));
                }
                model.addAttribute("omissions",omissions);
                model.addAttribute("students",students);
                return "secretary_menu";
            }
        }
        return "redirect:/authorization";
    }

    @GetMapping("/secretary_secretary_menu")
    public String secretary_secretary_menu(Model model, HttpSession session) {
        session.removeAttribute("groups");
        session.removeAttribute("filter_faculty");
        session.removeAttribute("filter_specialty");
        session.removeAttribute("filter_course");
        session.removeAttribute("filter_form");
        session.removeAttribute("search");
        session.removeAttribute("groups");
        session.removeAttribute("gr_info");
        Character user = (Character) session.getAttribute("user");
        if (user != null) {
            if (user == 's') {
                Iterable<Secretary> approved = secretaryService.getAllByStatus(1);
                Iterable<Secretary> waiting = secretaryService.getAllByStatus(0);
                model.addAttribute("approved", approved);
                model.addAttribute("waiting", waiting);
                return "secretary_secretary_menu";
            }
        }
        return "redirect:/authorization";
    }

    @PostMapping("/secretary_secretary_menu/approve")
    public String approve_secretary(Model model, HttpSession session, @RequestParam String login) {
        secretaryService.approve(login);
        model.addAttribute("success", "Секретарь" + login+" был добавлен в систему");
        return secretary_secretary_menu(model, session);
    }

    @PostMapping("/secretary_secretary_menu/delete")
    public String delete_secretary(Model model, HttpSession session, @RequestParam String login) {
        Secretary secretary = secretaryService.getByLogin(login);
        int status = secretary.getStatus();
        if (status == 0 || secretaryService.howManyAdmins() > 1) {
            secretaryService.delete(login);
        }
        if (status==0){
            model.addAttribute("success", "Заявка была удалена");
        } else model.addAttribute("success", "Секретарь был удален");
        return secretary_secretary_menu(model, session);
    }
}
