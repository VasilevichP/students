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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Controller
public class SecretaryStudentController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/secretary_student_menu")
    public String secretary_student_menu(Model model, HttpSession session) {
        session.removeAttribute("groups");
        session.removeAttribute("filter_faculty");
        session.removeAttribute("filter_specialty");
        session.removeAttribute("filter_course");
        session.removeAttribute("filter_form");
        session.removeAttribute("search");
        session.removeAttribute("groups");
        session.removeAttribute("gr_info");
        session.removeAttribute("gr_students");
        Character user = (Character) session.getAttribute("user");
        if (user != null) {
            if (user == 's') {
                if (session.getAttribute("students") == null) {
                    if (!model.containsAttribute("students")) {
                        Iterable<Student> students = studentService.getAllStudents();
                        model.addAttribute("students", students);
                        if (StreamSupport.stream(students.spliterator(), false).count() == 0)
                            model.addAttribute("noStudents", "В базе данных нет студентов");
                    }
                    ArrayList<String> faculties = new ArrayList<>();
                    for (Faculties f : Faculties.values()) {
                        faculties.add(f.getTitle());
                    }
                    model.addAttribute("faculties", faculties);
                    ArrayList<String> specialties = (ArrayList<String>) session.getAttribute("specialties");
                    if (specialties != null) {
                        System.out.println("chosen specs");
                        String fac = (String) session.getAttribute("faculty");
                        if (fac != null) {
                            session.removeAttribute("faculty");
                        }
                        session.removeAttribute("specialties");
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
                    model.addAttribute("specialties", specialties);
                    ArrayList<StudGroup> groups = (ArrayList<StudGroup>) session.getAttribute("grouplist");
                    if (groups != null) {
                        String spec = (String) session.getAttribute("specialty");
                        if (spec != null) session.removeAttribute("specialty");
                        model.addAttribute("specialty", spec);
                        session.removeAttribute("grouplist");
                    } else {
                        groups = new ArrayList<>();
                        String s = specialties.get(0);
                        if (model.containsAttribute("specialty"))
                            s = (String) model.getAttribute("specialty");
                        groups = groupService.findBySpecialty(s);
                        int cour;
                        if (model.containsAttribute("course")) {
                            cour = (int) model.getAttribute("course");
                        } else {
                            cour = 1;
                        }
                        groups.removeIf(g -> g.getCourse() != cour);
                    }
                    model.addAttribute("grouplist", groups);
                    model.addAttribute("filter_faculties", faculties);
                    if (!model.containsAttribute("filter_specialties")) {
                        ArrayList<String> filter_spec = new ArrayList<>();
                        for (Specialties s : Specialties.values()) {
                            filter_spec.add(s.getTitle());
                        }
                        model.addAttribute("filter_specialties", filter_spec);
                    }
                    if (!model.containsAttribute("filter_groups")) {
                        List<StudGroup> groupList = (List<StudGroup>) groupService.getAllGroups();
                        model.addAttribute("filter_groups", groupList);
                    }
                    return "secretary_student_menu";
                }
            }
        }
        return "redirect:/authorization";
    }

    @PostMapping("/secretary_student_menu/choose_specialty")
    public String chooseSpec(Model model, HttpSession session, @RequestParam String fac, @RequestParam String nam, @RequestParam String b_d, @RequestParam int gen, @RequestParam String ph,
                             @RequestParam String em, @RequestParam String reg_adr, @RequestParam String coun, @RequestParam String pas, @RequestParam String adr,
                             @RequestParam int cour, @RequestParam int typ) {
        Faculties f = Faculties.getInstance(fac);
        ArrayList<String> specialties = Specialties.getByFaculty(f);
        session.setAttribute("specialties", specialties);
        session.setAttribute("faculty", fac);
        model.addAttribute("name", nam);
        model.addAttribute("b_date", b_d);
        model.addAttribute("gender", gen);
        model.addAttribute("phone", ph);
        model.addAttribute("email", em);
        model.addAttribute("reg_address", reg_adr);
        model.addAttribute("country", coun);
        model.addAttribute("passport", pas);
        model.addAttribute("address", adr);
        model.addAttribute("course", cour);
        model.addAttribute("type", typ);
        return secretary_student_menu(model, session);
    }


    @PostMapping("/secretary_student_menu/choose_group")
    public String chooseGroup(Model model, HttpSession session, @RequestParam String fac, @RequestParam String spec, @RequestParam String nam, @RequestParam String b_d, @RequestParam int gen, @RequestParam String ph,
                              @RequestParam String em, @RequestParam String reg_adr, @RequestParam String coun, @RequestParam String pas, @RequestParam String adr,
                              @RequestParam int cour, @RequestParam int typ) {
        Faculties f = Faculties.getInstance(fac);
        System.out.println(fac + " " + spec);
        ArrayList<String> specialties = Specialties.getByFaculty(f);
        session.setAttribute("specialties", specialties);
        ArrayList<StudGroup> groups = groupService.findBySpecialty(spec);
        groups.removeIf(g -> g.getCourse() != cour);
        session.setAttribute("grouplist", groups);
        session.setAttribute("specialty", spec);
        session.setAttribute("faculty", fac);
        model.addAttribute("name", nam);
        model.addAttribute("b_date", b_d);
        model.addAttribute("gender", gen);
        model.addAttribute("phone", ph);
        model.addAttribute("email", em);
        model.addAttribute("reg_address", reg_adr);
        model.addAttribute("country", coun);
        model.addAttribute("passport", pas);
        model.addAttribute("address", adr);
        model.addAttribute("course", cour);
        model.addAttribute("type", typ);
        return secretary_student_menu(model, session);
    }

    @PostMapping("/secretary_student_menu/add_student")
    public String secretary_student_add(Model model, HttpSession session, @RequestParam String name, @RequestParam String b_date,
                                        @RequestParam int gender, @RequestParam String phone, @RequestParam String email, @RequestParam String reg_address,
                                        @RequestParam String country, @RequestParam String passport, @RequestParam String address, @RequestParam int course,
                                        @RequestParam Optional<Long> group, @RequestParam String faculty, @RequestParam String specialty,
                                        @RequestParam int type, @RequestParam("photo") MultipartFile file) {
        Character user = (Character) session.getAttribute("user");
        if (user != null) {
            if (user == 's') {
                boolean canAdd = true;
                if (studentService.findByName(name)) {
                    model.addAttribute("name_error", "Студент с таким именем уже есть в системе");
                    canAdd = false;
                }
                if (studentService.findByEmail(email)) {
                    model.addAttribute("email_error", "Студент с таким адресом эл. почты уже есть в системе");
                    canAdd = false;
                }
                if (studentService.findByPhone(phone)) {
                    model.addAttribute("phone_error", "Студент с таким номером телефона уже есть в системе");
                    canAdd = false;
                }
                if (studentService.findByPassport(passport)) {
                    model.addAttribute("passport_error", "Студент с таким номером паспорта уже есть в системе");
                    canAdd = false;
                }
                if (group.isEmpty()) {
                    model.addAttribute("group_error", "Нет подходящей для студента группы");
                    canAdd = false;
                }
                if (canAdd) {
                    try {
                        Student student = new Student("", "", email, name, LocalDate.parse(b_date), group.get(), gender, phone, passport, address,
                                country, reg_address, faculty, specialty, course, type);
                        if (type==2) student.setScholarship(400.00);
                        System.out.println("can save");
                        studentService.addOrChangeStudent(student);
                        model.addAttribute("success", "Студент был добавлен в базу данных");
                        if (!file.isEmpty()) {
                            try {
                                System.out.println(file.getOriginalFilename());
                                Student stud = studentService.getByEmail(email);
                                long id = stud.getId();
                                File f = new File("D:\\Work\\Java\\student_account\\demo(1)\\demo\\src\\main\\resources\\static\\images\\student_photos\\" + id + ".png");
                                System.out.println("file created");
                                file.transferTo(f);
                                System.out.println("file copied");
                                stud.setPhoto(id + ".png");
                                studentService.addOrChangeStudent(stud);
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("exception");
                        session.setAttribute("specialty", specialty);
                        session.setAttribute("faculty", faculty);
                        model.addAttribute("name", name);
                        model.addAttribute("b_date", b_date);
                        model.addAttribute("gender", gender);
                        model.addAttribute("phone", phone);
                        model.addAttribute("email", email);
                        model.addAttribute("reg_address", reg_address);
                        model.addAttribute("country", country);
                        model.addAttribute("passport", passport);
                        model.addAttribute("address", address);
                        model.addAttribute("course", course);
                        model.addAttribute("group", group);
                        model.addAttribute("type", type);
                        throw new RuntimeException(e);
                    }
                } else {
                    session.setAttribute("specialty", specialty);
                    session.setAttribute("faculty", faculty);
                    model.addAttribute("name", name);
                    model.addAttribute("b_date", b_date);
                    model.addAttribute("gender", gender);
                    model.addAttribute("phone", phone);
                    model.addAttribute("email", email);
                    model.addAttribute("reg_address", reg_address);
                    model.addAttribute("country", country);
                    model.addAttribute("passport", passport);
                    model.addAttribute("address", address);
                    model.addAttribute("course", course);
                    if (group.isPresent())
                        model.addAttribute("group", group);
                    model.addAttribute("type", type);
                }
                return secretary_student_menu(model, session);
            }
            ;
        }
        return "redirect:/authorization";
    }

    @GetMapping("/secretary_student_menu/{id:\\d+}")
    public String toStudentInfo(@PathVariable(value = "id") long id, Model model, HttpSession session) {
        try {
            Student student = studentService.getById(id);
            model.addAttribute("student", student);
            ArrayList<String> faculties = new ArrayList<>();
            for (Faculties f : Faculties.values()) {
                faculties.add(f.getTitle());
            }
            ArrayList<String> specialties = new ArrayList<>();
            Faculties f = Faculties.getInstance(student.getFaculty());
            for (Specialties s : Specialties.values()) {
                if (s.getFaculty().equals(f)) specialties.add(s.getTitle());
            }
            String s = student.getSpecialty();
            ArrayList<StudGroup> groups = groupService.findBySpecialty(s);
            int cour = student.getCourse();
            groups.removeIf(g -> g.getCourse() != cour);
            model.addAttribute("faculties", faculties);
            model.addAttribute("specialties", specialties);
            model.addAttribute("grouplist", groups);
            return "secretary_student_info";
        } catch (Exception e) {
            return secretary_student_menu(model, session);
        }
    }

    @PostMapping("/secretary_student_menu/{id:\\d+}/choose_specialty")
    public String chooseStudSpec(Model model, HttpSession session, @PathVariable int id, @RequestParam String fac, @RequestParam String nam, @RequestParam String b_d, @RequestParam int gen, @RequestParam String ph,
                                 @RequestParam String em, @RequestParam String reg_adr, @RequestParam String coun, @RequestParam String pas, @RequestParam String adr,
                                 @RequestParam int cour, @RequestParam int typ) {
        System.out.println(b_d);
        Student student = studentService.getById(id);
        Faculties faculty = Faculties.getInstance(fac);
        ArrayList<String> specialties = Specialties.getByFaculty(faculty);
        ArrayList<String> faculties = new ArrayList<>();
        for (Faculties f : Faculties.values()) faculties.add(f.getTitle());
        String s = specialties.get(0);
        ArrayList<StudGroup> groups = groupService.findBySpecialty(s);
        System.out.println(groups.size());
        groups.removeIf(g -> g.getCourse() != cour);
        System.out.println(groups.size());
        model.addAttribute("faculties", faculties);
        model.addAttribute("specialties", specialties);
        model.addAttribute("grouplist", groups);
        if (!groups.isEmpty()) student.setGroupnumber(groups.get(0).getGr_num());
        student.setFaculty(fac);
        student.setSpecialty(specialties.get(0));
        student.setName(nam);
        student.setGender(gen);
        if (b_d != null && !b_d.isEmpty()) student.setBirth_date(LocalDate.parse(b_d));
        student.setPhone(ph);
        student.setEmail(em);
        student.setRegistration_address(reg_adr);
        student.setCountry(coun);
        student.setPassport(pas);
        student.setAddress(adr);
        student.setCourse(cour);
        student.setType_of_study(typ);
        model.addAttribute("student", student);
        return "secretary_student_info";
    }

    @PostMapping("/secretary_student_menu/{id:\\d+}/choose_group")
    public String chooseStudGroup(Model model, HttpSession session, @PathVariable int id, @RequestParam String fac, @RequestParam String spec,
                                  @RequestParam String nam, @RequestParam String b_d, @RequestParam int gen, @RequestParam String ph,
                                  @RequestParam String em, @RequestParam String reg_adr, @RequestParam String coun, @RequestParam String pas, @RequestParam String adr,
                                  @RequestParam int cour, @RequestParam int typ) {
        Faculties faculty = Faculties.getInstance(fac);
        ArrayList<String> specialties = Specialties.getByFaculty(faculty);
        model.addAttribute("specialties", specialties);
        ArrayList<String> faculties = new ArrayList<>();
        for (Faculties f : Faculties.values()) faculties.add(f.getTitle());
        model.addAttribute("faculties", faculties);
        ArrayList<StudGroup> groups = groupService.findBySpecialty(spec);
        groups.removeIf(g -> g.getCourse() != cour);
        model.addAttribute("grouplist", groups);
        Student student = studentService.getById(id);
        student.setFaculty(fac);
        student.setSpecialty(spec);
        student.setName(nam);
        student.setGender(gen);
        student.setPhone(ph);
        student.setEmail(em);
        student.setRegistration_address(reg_adr);
        student.setCountry(coun);
        student.setPassport(pas);
        student.setAddress(adr);
        student.setCourse(cour);
        student.setType_of_study(typ);
        model.addAttribute("student", student);
        return "secretary_student_info";
    }

    @PostMapping("/secretary_student_menu/{id:\\d+}/change")
    public String secretary_student_change(Model model, HttpSession session, @PathVariable int id, @RequestParam String name, @RequestParam String b_date,
                                           @RequestParam int gender, @RequestParam String phone, @RequestParam String email, @RequestParam String reg_address,
                                           @RequestParam String country, @RequestParam String passport, @RequestParam String address, @RequestParam int course,
                                           @RequestParam Optional<Long> group, @RequestParam String faculty, @RequestParam String specialty,
                                           @RequestParam int type,@RequestParam Optional<Double> scholarship, @RequestParam("photo") MultipartFile file) {
        Student student = studentService.getById(id);
        boolean canAdd = true;
        if (studentService.findByName(name) && !studentService.getByName(name).getId().equals(student.getId())) {
            model.addAttribute("name_error", "Студент с таким именем уже есть в системе");
            canAdd = false;
        }
        if (studentService.findByEmail(email) && !studentService.getByEmail(email).getId().equals(student.getId())) {
            model.addAttribute("email_error", "Студент с таким адресом эл. почты уже есть в системе");
            canAdd = false;
        }
        if (studentService.findByPhone(phone) && !studentService.getByPhone(phone).getId().equals(student.getId())) {
            model.addAttribute("phone_error", "Студент с таким номером телефона уже есть в системе");
            canAdd = false;
        }
        if (studentService.findByPassport(passport) && !studentService.getByPassport(passport).getId().equals(student.getId())) {
            model.addAttribute("passport_error", "Студент с таким номером паспорта уже есть в системе");
            canAdd = false;
        }
        if (group.isEmpty()) {
            model.addAttribute("group_error", "Нет подходящей для студента группы");
            canAdd = false;
        } else student.setGroupnumber(group.get());
        student.setFaculty(faculty);
        student.setSpecialty(specialty);
        student.setName(name);
        student.setBirth_date(LocalDate.parse(b_date));
        student.setGender(gender);
        student.setPhone(phone);
        student.setEmail(email);
        student.setRegistration_address(reg_address);
        student.setCountry(country);
        student.setPassport(passport);
        student.setAddress(address);
        student.setCourse(course);
        student.setType_of_study(type);
        if (type==2) {
            if(scholarship.isPresent())
            student.setScholarship(scholarship.get());
            else student.setScholarship(400.00);
        }else student.setScholarship(0);
        Faculties fac = Faculties.getInstance(faculty);
        ArrayList<String> specialties = Specialties.getByFaculty(fac);
        model.addAttribute("specialties", specialties);
        ArrayList<String> faculties = new ArrayList<>();
        for (Faculties f : Faculties.values()) faculties.add(f.getTitle());
        model.addAttribute("faculties", faculties);
        ArrayList<StudGroup> groups = groupService.findBySpecialty(specialty);
        groups.removeIf(g -> g.getCourse() != course);
        model.addAttribute("grouplist", groups);
        model.addAttribute("student", student);
        if (canAdd) {
            if (!file.isEmpty()) {
                try {
                    File f = new File("D:\\Work\\Java\\student_account\\demo(1)\\demo\\src\\main\\resources\\static\\images\\student_photos\\" + id + ".png");
                    System.out.println("file created");
                    file.transferTo(f);
                    System.out.println("file copied");
                    student.setPhoto(id + ".png");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            studentService.addOrChangeStudent(student);
            model.addAttribute("success", "Информация о студенте была обновлена");
        }
        return "secretary_student_info";
    }

    @PostMapping("/secretary_student_menu/{id:\\d+}/delete")
    public String deleteStudent(Model model, HttpSession session, @PathVariable int id) {
        try {
            studentService.deleteStudent(id);
            model.addAttribute("success", "Информация о студенте была удалена");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return secretary_student_menu(model, session);
    }

    @PostMapping("/secretary_student_menu/filter")
    public String filter(Model model, HttpSession session, @RequestParam String search, @RequestParam int filter_course,
                         @RequestParam String filter_faculty, @RequestParam String filter_specialty, @RequestParam int filter_group) {
        List<Student> students = (List<Student>) studentService.getAllStudents();
        if (!search.isEmpty()) {
            students = studentService.search(students, search);
            model.addAttribute("search", search);
        }
        if (filter_course != 0) {
            students = studentService.filterByCourse(students, filter_course);
            model.addAttribute("filter_course", filter_course);
        }
        if (!filter_faculty.isEmpty()) {
            students = studentService.filterByFaculty(students, filter_faculty);
            model.addAttribute("filter_faculty", filter_faculty);
            ArrayList<String> sp = new ArrayList<>();
            Faculties f = Faculties.getInstance(filter_faculty);
            for (Specialties s : Specialties.values()) {
                if (s.getFaculty().equals(f)) sp.add(s.getTitle());
            }
            model.addAttribute("filter_specialties", sp);
            if (filter_specialty.isEmpty()) {
                ArrayList<StudGroup> groups = new ArrayList<>();
                for (String s : sp) {
                    ArrayList<StudGroup> grs = groupService.findBySpecialty(s);
                    groups.addAll(grs);
                }
                model.addAttribute("filter_groups", groups);
            }
        }
        if (!filter_specialty.isEmpty()) {
            students = studentService.filterBySpecialty(students, filter_specialty);
            model.addAttribute("filter_specialty", filter_specialty);
            ArrayList<StudGroup> groups = groupService.findBySpecialty(filter_specialty);
            if (filter_course != 0) {
                groups.removeIf(g -> g.getCourse() != filter_course);
            }
            model.addAttribute("filter_groups", groups);
        }
        if (filter_group != 0) {
            students = studentService.filterByGroup(students, filter_group);
            model.addAttribute("filter_group", filter_group);
        }
        model.addAttribute("students", students);
        if (students.isEmpty()) model.addAttribute("noStudents", "Не найдено подходящих студентов");
        return secretary_student_menu(model, session);
    }

    @GetMapping("/secretary_student_menu/{id:\\d+}/grades")
    public String toStudentGrades(@PathVariable(value = "id") long id, Model model, HttpSession session) {
        Student student = studentService.getById(id);
        StudGroup group = groupService.getByNumber(student.getGroupnumber());
        List<Schedule> schedules = scheduleService.getByGroup(group.getGr_num());
        ArrayList<String> subjects = new ArrayList<>();
        ArrayList<Double> averages = new ArrayList<>();
        ArrayList<Long> skips = new ArrayList<>();
        for (Schedule s : schedules) {
            double average = scheduleService.getStudentScheduleAverage(student.getId(), s.getId());
            long skip = scheduleService.countStudentSkipsBySchedule(student.getId(), s.getId());
            if (!subjects.contains(s.getSubject())) {
                subjects.add(s.getSubject());
                averages.add(average);
                skips.add(skip);
            } else {
                int index = subjects.indexOf(s.getSubject());
                double old_av = averages.get(index) + average;
                averages.set(index, old_av);
                long old_sk = skips.get(index) + skip;
                skips.set(index, old_sk);
            }
        }
        model.addAttribute("general_average", student.getAverage_mark());
        model.addAttribute("all_skips", student.getSkips());
        model.addAttribute("subjects", subjects);
        model.addAttribute("averages", averages);
        model.addAttribute("skips", skips);
        model.addAttribute("student", student);
        return "secretary_student_grades";
    }

    @GetMapping("/secretary_student_menu/{id:\\d+}/omissions")
    public String toStudentOmissions(@PathVariable(value = "id") long id, Model model, HttpSession session) {
        Student student = studentService.getById(id);
        model.addAttribute("student", student);
        List<Omission> omissions = scheduleService.getStudentOmissions(id);
        model.addAttribute("omissions", omissions);
        return "secretary_student_omissions";
    }

    @PostMapping("/secretary_student_menu/{id:\\d+}/omissions/delete")
    public String deleteOmission(@PathVariable(value = "id") long id, @RequestParam int om_id, @RequestParam int page, Model model, HttpSession session) {
        Omission omission = scheduleService.getOmissionById(om_id);
        omission.setStatus(2);
        scheduleService.saveOmission(omission);
        if (page == 2) {
            Student student = studentService.getById(id);
            model.addAttribute("student", student);
            List<Omission> omissions = scheduleService.getStudentOmissions(id);
            model.addAttribute("omissions", omissions);
            return "secretary_student_omissions";
        }
        else {
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

    @PostMapping("/secretary_student_menu/{id:\\d+}/omissions/approve")
    public String approveOmission(@PathVariable(value = "id") long id, @RequestParam int om_id, @RequestParam int page, Model model, HttpSession session) {
        Omission omission = scheduleService.getOmissionById(om_id);
        omission.setStatus(1);
        scheduleService.approveOmission(omission);
        if (page == 2) {
            Student student = studentService.getById(id);
            model.addAttribute("student", student);
            List<Omission> omissions = scheduleService.getStudentOmissions(id);
            model.addAttribute("omissions", omissions);
            return "secretary_student_omissions";
        }
        else {
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
}
