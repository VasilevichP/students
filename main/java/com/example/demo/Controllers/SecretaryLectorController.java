package com.example.demo.Controllers;

import com.example.demo.Entities.Lector;
import com.example.demo.Entities.StudGroup;
import com.example.demo.Entities.Subject;
import com.example.demo.Services.GroupService;
import com.example.demo.Services.LectorService;
import com.example.demo.Services.StudentService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Controller
public class SecretaryLectorController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private LectorService lectorService;

    @GetMapping("/secretary_lector_menu")
    public String secretary_lector_menu(Model model, HttpSession session) {
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
                Iterable<Lector> lectors = lectorService.getAllLectors();
                model.addAttribute("lectors", lectors);
                if (StreamSupport.stream(lectors.spliterator(), false).count() == 0)
                    model.addAttribute("noLectors", "В базе данных нет преподавателей");
                Iterable<StudGroup> groups = groupService.getAllGroups();
                ArrayList<String> subjects = new ArrayList<>();
                for (Subject sub : Subject.values()) {
                    subjects.add(sub.getTitle());
                }
                model.addAttribute("groups", groups);
                model.addAttribute("subjects", subjects);
                return "secretary_lector_menu";
            }
        }
        return "redirect:/authorization";
    }

    @PostMapping("/secretary_lector_menu/add_lector")
    public String addLector(Model model, HttpSession session, @RequestParam String name, @RequestParam String email,
                            @RequestParam String groups, @RequestParam String subjects, @RequestParam("photo") MultipartFile file) {
        boolean canAdd = true;
        if (lectorService.existsByName(name)) {
            model.addAttribute("name_error", "Преподаватель с таким именем уже есть");
            canAdd = false;
        }
        if (lectorService.existsByEmail(email) || studentService.findByEmail(email)) {
            model.addAttribute("email_error", "Данный адрес электронной почты занят");
            canAdd = false;
        }
        if (canAdd) {
            String[] grs = groups.split(",");
            String[] sbjcts = subjects.split(",");
            ArrayList<Long> grps_nums = new ArrayList<>();
            for (String s : grs) {
                grps_nums.add(Long.parseLong(s));
            }
            Lector lector = new Lector(name, email);
            lectorService.addLector(lector);
            lector = lectorService.getByEmail(email);
            long id = lector.getId();
            for (String s : sbjcts) {
                lectorService.addSubjects(id, s);
            }
            for (long g : grps_nums) {
                lectorService.addGroups(id, g);
            }
            if (!file.isEmpty()) {
                try {
                    System.out.println(file.getOriginalFilename());
                    File f = new File("D:\\Work\\Java\\student_account\\demo(1)\\demo\\src\\main\\resources\\static\\images\\lector_photos\\" + id + ".png");
                    System.out.println("file created");
                    file.transferTo(f);
                    System.out.println("file copied");
                    lector.setPhoto(id + ".png");
                    lectorService.addLector(lector);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return secretary_lector_menu(model, session);
    }

    @GetMapping("/secretary_lector_menu/{id:\\d+}")
    public String toLectorInfo(Model model, HttpSession session, @PathVariable(value = "id") long id) {
        try {
            Lector lector = lectorService.getById(id);
            ArrayList<Long> chosen_groups = lectorService.getGroups(id);
            ArrayList<String> chosen_subjects = lectorService.getSubjects(id);
            Iterable<StudGroup> groups = groupService.getAllGroups();
            ArrayList<String> subjects = new ArrayList<>();
            for (Subject sub : Subject.values()) {
                subjects.add(sub.getTitle());
            }
            String strGroups = lectorService.getStringGroups(id);
            String strSubjects = lectorService.getStringSubjects(id);
            model.addAttribute("lector", lector);
            model.addAttribute("chosen_groups", chosen_groups);
            model.addAttribute("chosen_subjects", chosen_subjects);
            model.addAttribute("groups", groups);
            model.addAttribute("subjects", subjects);
            model.addAttribute("strgroups", strGroups);
            model.addAttribute("strsubjects", strSubjects);
            return "secretary_lector_info";
        } catch (Exception e) {
            return secretary_lector_menu(model, session);
        }
    }
    @PostMapping("/secretary_lector_menu/{id:\\d+}/change")
    public String changeLector(Model model, HttpSession session, @PathVariable int id,@RequestParam String name, @RequestParam String email,
                               @RequestParam String groups, @RequestParam String subjects, @RequestParam("photo") MultipartFile file){
        System.out.println("in change lector");
        Iterable<StudGroup> all_groups = groupService.getAllGroups();
        ArrayList<String> all_subjects = new ArrayList<>();
        for (Subject sub : Subject.values()) {
            all_subjects.add(sub.getTitle());
        }
        List<String> grs = List.of(groups.split(","));
        List<String> sbs = List.of(subjects.split(","));
        ArrayList<Long> grps_nums = new ArrayList<>();
        for (String s : grs) {
            grps_nums.add(Long.parseLong(s));
        }
        Lector lector = lectorService.getById(id);
        lector.setName(name);
        lector.setEmail(email);
        model.addAttribute("lector", lector);
        model.addAttribute("chosen_groups", grps_nums);
        model.addAttribute("chosen_subjects", sbs);
        model.addAttribute("lector", lector);
        model.addAttribute("groups", all_groups);
        model.addAttribute("subjects", all_subjects);
        model.addAttribute("strgroups", groups);
        model.addAttribute("strsubjects", subjects);
        boolean canAdd = true;
        if (lectorService.existsByName(name) && !lectorService.getByName(name).getId().equals((long) id)) {
            model.addAttribute("name_error", "Преподаватель с таким именем уже есть");
            canAdd = false;
        }
        if ((lectorService.existsByEmail(email) || studentService.findByEmail(email)) && !lectorService.getByEmail(email).getId().equals((long) id)) {
            model.addAttribute("email_error", "Данный адрес электронной почты занят");
            canAdd = false;
        }
        if(canAdd){
            if (!file.isEmpty()) {
                try {
                    File f = new File("D:\\Work\\Java\\student_account\\demo(1)\\demo\\src\\main\\resources\\static\\images\\lector_photos\\" + id + ".png");
                    System.out.println("file created");
                    file.transferTo(f);
                    System.out.println("file copied");
                    lector.setPhoto(id + ".png");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            lectorService.saveLector(lector);
            for (String s : sbs) {
                lectorService.addSubjects(id, s);
            }
            for (long g : grps_nums) {
                lectorService.addGroups(id, g);
            }
        }
        return "secretary_lector_info";
    }

    @PostMapping("/secretary_lector_menu/{id:\\d+}/delete")
    public String deleteStudent(Model model, HttpSession session, @PathVariable int id){
        try{
            lectorService.deleteLector(id);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return secretary_lector_menu(model, session);
    }
}
