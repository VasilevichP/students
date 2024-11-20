package com.example.demo.Controllers;

import com.example.demo.Services.SecretaryService;
import com.example.demo.Services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentController {
    @Autowired
    private StudentService studentService;
    @GetMapping("/student_menu")
    public String student_menu(Model model){
        return "student_menu";
    }
}
