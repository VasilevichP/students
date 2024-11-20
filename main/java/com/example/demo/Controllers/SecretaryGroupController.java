package com.example.demo.Controllers;

import com.example.demo.Entities.Faculties;
import com.example.demo.Entities.Specialties;
import com.example.demo.Entities.StudGroup;
import com.example.demo.Entities.Student;
import com.example.demo.Services.GroupService;
import com.example.demo.Services.LectorService;
import com.example.demo.Services.SecretaryService;
import com.example.demo.Services.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class SecretaryGroupController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private StudentService studentService;

    @GetMapping("/secretary_group_menu")
    public String secretary_group_menu(Model model, HttpSession httpSession) {
        Character user = (Character) httpSession.getAttribute("user");
        if (user != null) {
            if (user == 's') {
                if (httpSession.getAttribute("groups") == null) {
                    Iterable<StudGroup> groups = groupService.getAllGroups();
                    model.addAttribute("groups", groups);
                    if (StreamSupport.stream(groups.spliterator(), false).count() == 0)
                        model.addAttribute("noGroups", "Список групп пуст");
                } else model.addAttribute("groups", httpSession.getAttribute("groups"));
                ArrayList<String> faculties = new ArrayList<>();
                ArrayList<String> filter_faculties = new ArrayList<>();
                for (Faculties f : Faculties.values()) {
                    faculties.add(f.getTitle());
                }
                filter_faculties = faculties;
                model.addAttribute("filter_faculties", filter_faculties);
                ArrayList<String> specialties = (ArrayList<String>) httpSession.getAttribute("specialties");
                if (specialties != null) {
                    String fac = (String) httpSession.getAttribute("faculty");
                    if (fac != null) httpSession.removeAttribute("faculty");
                    httpSession.removeAttribute("specialties");
                    model.addAttribute("faculty", fac);
                } else {
                    specialties = new ArrayList<>();
                    Faculties f = Faculties.values()[0];
                    if (model.containsAttribute("faculty"))
                        f = Faculties.getInstance((String) model.getAttribute("faculty"));
                    for (Specialties s : Specialties.values()) {
                        if (s.getFaculty().equals(f)) specialties.add(s.getTitle());
                    }
                }
                if (httpSession.getAttribute("search") != null)
                    model.addAttribute("search", httpSession.getAttribute("search"));
                if (httpSession.getAttribute("filter_course") != null)
                    model.addAttribute("filter_course", httpSession.getAttribute("filter_course"));
                if (httpSession.getAttribute("filter_form") != null)
                    model.addAttribute("filter_form", httpSession.getAttribute("filter_form"));
                ArrayList<String> filter_specialties = new ArrayList<>();
                if (httpSession.getAttribute("filter_faculty") != null && !httpSession.getAttribute("filter_faculty").equals("-")) {
                    String f = (String) httpSession.getAttribute("filter_faculty");
                    for (Specialties s : Specialties.values()) {
                        if (s.getFaculty().getTitle().equals(f)) filter_specialties.add(s.getTitle());
                    }
                    model.addAttribute("filter_faculty", httpSession.getAttribute("filter_faculty"));
                } else {
                    for (Specialties s : Specialties.values()) {
                        filter_specialties.add(s.getTitle());
                    }
                }
                if (httpSession.getAttribute("gr_info") != null)
                    model.addAttribute("gr_info", httpSession.getAttribute("gr_info"));
                if (httpSession.getAttribute("gr_students") != null)
                    model.addAttribute("gr_students", httpSession.getAttribute("gr_students"));
                model.addAttribute("filter_specialties", filter_specialties);
                if (httpSession.getAttribute("filter_specialty") != null)
                    model.addAttribute("filter_specialty", httpSession.getAttribute("filter_specialty"));
                model.addAttribute("specialties", specialties);
                model.addAttribute("faculties", faculties);
                return "secretary_group_menu";
            }
        }
        return "redirect:/authorization";
    }

    @PostMapping("/secretary_group_menu/choose_specialty")
    public String chooseSpec(Model model, HttpSession session, @RequestParam String fac, @RequestParam Optional<Long> num) {
        Faculties f = Faculties.getInstance(fac);
        ArrayList<String> specialties = Specialties.getByFaculty(f);
        session.setAttribute("specialties", specialties);
        session.setAttribute("faculty", fac);
        if (num.isPresent()) {
            model.addAttribute("groupNumber", num.get());
        }
        return secretary_group_menu(model, session);
    }

    @PostMapping("/secretary_group_menu/add_group")
    public String addGroup(Model model, HttpSession session, @RequestParam long number, @RequestParam String faculty, @RequestParam String specialty, @RequestParam int form) {
        if (groupService.doesExist(number)) {
            model.addAttribute("inputError", "Группа с таким номером уже существует");
            model.addAttribute("faculty", faculty);
            model.addAttribute("specialty", specialty);
            model.addAttribute("groupNumber", number);
            model.addAttribute("form", form);
        } else {
            System.out.println(form);
            StudGroup group = new StudGroup(number, specialty, 1, faculty, form);
            System.out.println(group.getForm_of_study());
            groupService.addGroup(group);
        }
        return secretary_group_menu(model, session);
    }

    @PostMapping("/secretary_group_menu/filter")
    public String filterGroups(Model model, HttpSession session, @RequestParam Optional<Integer> search, @RequestParam int filter_course, @RequestParam String filter_faculty, @RequestParam String filter_specialty, @RequestParam int filter_form) {
        ArrayList<StudGroup> groups = (ArrayList<StudGroup>) StreamSupport.stream(groupService.getAllGroups().spliterator(), false)
                .collect(Collectors.toList());
        System.out.println(groups.size());
        session.setAttribute("filter_course", filter_course);
        if (filter_course != 0) {
            groups = groupService.filterByCourse(groups, filter_course);
            System.out.println(groups.size());
        }
        session.setAttribute("filter_faculty", filter_faculty);
        if (!filter_faculty.equals("-")) {
            System.out.println(filter_faculty);
            groups = groupService.filterByFaculty(groups, filter_faculty);
            System.out.println(groups.size());
        }
        session.setAttribute("filter_specialty", filter_specialty);
        if (!filter_specialty.equals("-")) {
            groups = groupService.filterBySpecialty(groups, filter_specialty);
            System.out.println(groups.size());
        }
        session.setAttribute("filter_form", filter_form);
        if (filter_form != 0) {
            groups = groupService.filterByForm(groups, filter_form);
            System.out.println(groups.size());
        }
        if (search.isPresent()) {
            System.out.println("search: " + search.get());
            groups = groupService.searchByNumber(groups, search.get());
            System.out.println(groups.size());
            session.setAttribute("search", search.get());
        } else {
            System.out.println("no search");
            if (session.getAttribute("search") != null)
                session.removeAttribute("search");
        }
        session.setAttribute("groups", groups);
        if (groups.isEmpty())
            model.addAttribute("noGroups", "Список групп пуст");
        return secretary_group_menu(model, session);
    }

    @PostMapping("/secretary_group_menu/get_group")
    public String getGroup(Model model, HttpSession session, @RequestParam long number) {
        StudGroup group = groupService.getByNumber(number);
        ArrayList<Student> students = studentService.findByGroup(group.getGr_num());
        System.out.println(students.size());
        session.setAttribute("gr_students", students);
        session.setAttribute("gr_info", group);
        return secretary_group_menu(model, session);
    }

    @PostMapping("/secretary_group_menu/change_course")
    public String changeGroupCourse(Model model, HttpSession session, @RequestParam long number, @RequestParam int course, @RequestParam int form) {
        StudGroup group = groupService.getByNumber(number);
        group.setCourse(course);
        group.setForm_of_study(form);
        groupService.changeGroup(group);
        if (session.getAttribute("groups") != null) {
            ArrayList<StudGroup> groups = (ArrayList<StudGroup>) session.getAttribute("groups");
            for (StudGroup g : groups) {
                if (g.getGr_num() == number) {
                    g.setCourse(course);
                    break;
                }
            }
            session.setAttribute("groups", groups);
        }
        session.setAttribute("gr_info", group);
        return secretary_group_menu(model, session);
    }

    @PostMapping("/secretary_group_menu/delete_group")
    public String deleteGroup(Model model, HttpSession session, @RequestParam long number) {
            groupService.deleteGroup(number);
            System.out.println("del success");
            if (session.getAttribute("groups") != null) {
                ArrayList<StudGroup> groups = (ArrayList<StudGroup>) session.getAttribute("groups");
                for (StudGroup g : groups) {
                    if (g.getGr_num() == number) groups.remove(g);
                    break;
                }
                session.setAttribute("groups", groups);
            }
            session.removeAttribute("gr_info");
            session.removeAttribute("gr_students");
        return secretary_group_menu(model, session);
    }
}
