package com.example.demo.Controllers;

import com.example.demo.Entities.Faculties;
import com.example.demo.Entities.Lector;
import com.example.demo.Entities.Secretary;
import com.example.demo.Entities.Student;
import com.example.demo.Repositories.SecretaryRepositary;
import com.example.demo.Services.AuthRegService;
import com.example.demo.Services.LectorService;
import com.example.demo.Services.StudentService;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Random;

@Controller
public class AuthRegController {
    @Autowired
    private AuthRegService authRegService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private LectorService lectorService;

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
            return "redirect:/secretary_student_menu";
        }
        if (o instanceof Lector) {
            httpSession.setAttribute("user",((Lector) o).getLogin());
            return "redirect:/lector_menu";
        }
        if (o instanceof Student) {
            httpSession.setAttribute("user",((Student) o).getLogin());
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
                model.addAttribute("error", "Неправильный номер ученического билета");
                break;
            case 4:
                model.addAttribute("error", "Неправильный ID");
                break;
            case 5:
                model.addAttribute("error", "Данный логин уже занят");
                break;
            case 8:
                model.addAttribute("emailerr", "Несоответствие адресов эл. почты");
                break;
            case 9:
                model.addAttribute("error", "Вы уже имеете свой личный кабинет");
                break;
            case 10:
                model.addAttribute("passerror", "Пароль должен быть от 8 до 50 символов и содержать хотя бы 1 цифру");
                break;
            case 0:
                return "redirect:/authorization";
            default:
        }
        return "registration";
    }

    @GetMapping("/password_reset")
    public String passwordReset(Model model, HttpSession session) {
        return "password_forgotten";
    }

    @PostMapping("/password_reset/send_email")
    public String sendEmail(Model model, HttpSession session, @RequestParam String login, @RequestParam String email){
        model.addAttribute("login",login);
        model.addAttribute("email",email);
        boolean can_get = true;
        if (!(studentService.findByEmail(email)||lectorService.existsByEmail(email))) {
            can_get = false;
            model.addAttribute("error", "В системе нет пользователя с таким email");
        }
        if (!(studentService.findByLogin(login)||lectorService.existsByLogin(login))) {
            can_get = false;
            model.addAttribute("error", "В системе нет пользователя с таким логином");
        }
        if (can_get && !(studentService.getByLogin(login).getEmail().equals(email))){
            can_get = false;
            model.addAttribute("error", "Адрес эл. почты не соответствует введенному логину");
        }
        if (can_get){
            Random rn = new Random();
            int code = rn.nextInt(900000)+100000;
            session.setAttribute("code",code);
            authRegService.sendMail(email,code);
            System.out.println(code);
            return "password_verification";
        }
        return "password_forgotten";
    }
    @PostMapping("/password_reset/send_again")
    public String sendEmailAgain(Model model, HttpSession session, @RequestParam String login, @RequestParam String email){
        model.addAttribute("login",login);
        model.addAttribute("email",email);
        Random rn = new Random();
        int code = rn.nextInt(900000)+100000;
        session.setAttribute("code",code);
        System.out.println(code);
        return "password_verification";
    }
    @PostMapping("/password_reset/new_password")
    public String toPasswordChange(Model model, HttpSession session,@RequestParam int code, @RequestParam String login, @RequestParam String email){
        model.addAttribute("login",login);
        model.addAttribute("email",email);
        int orig_code = (int) session.getAttribute("code");
        if (orig_code==code) return "password_reset";
        else {
            model.addAttribute("code_error","Введен неправильный код подтверждения");
            return "password_forgotten";
        }
    }
    @PostMapping("/password_reset/reset_password")
    public String resetPassword(Model model, HttpSession session, @RequestParam String login, @RequestParam String password, @RequestParam String password2){
        boolean can_change = true;
        if (!password.equals(password2)){
            can_change = false;
            model.addAttribute("error","Несоответствие введенного и подтвержденного паролей");
        }
        int pass_check = authRegService.checkPassword(password);
        if (pass_check!=0){
            can_change = false;
            model.addAttribute("error","Пароль должен быть от 8 до 50 символов и содержать хотя бы 1 цифру");
        }
        if (can_change){
            String salt = BCrypt.gensalt();
            String hashedPassword = BCrypt.hashpw(password, salt);
            if (studentService.findByLogin(login)){
                Student student = studentService.getByLogin(login);
                student.setPassword(hashedPassword);
                studentService.addOrChangeStudent(student);
            }
            if(lectorService.existsByLogin(login)){
                Lector lector = lectorService.getByLogin(login);
                lector.setPassword(hashedPassword);
                lectorService.addLector(lector);
            }
            return "authorization";
        }
        else {
            model.addAttribute("login",login);
            model.addAttribute("password",password);
            model.addAttribute("password2",password2);
            return "password_reset";
        }
    }
}
