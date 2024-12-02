package com.example.demo.Controllers;

import com.example.demo.Entities.Omission;
import com.example.demo.Entities.Schedule;
import com.example.demo.Entities.StudGroup;
import com.example.demo.Entities.Student;
import com.example.demo.Services.GroupService;
import com.example.demo.Services.ScheduleService;
import com.example.demo.Services.SecretaryService;
import com.example.demo.Services.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class StudentController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private GroupService groupService;

    @GetMapping("/student_menu")
    public String student_menu(Model model, HttpSession session) {
        String user = (String) session.getAttribute("user");
        if (user != null && !user.isEmpty()) {
            if (studentService.findByLogin(user)) {
                Student student = studentService.getByLogin(user);
                model.addAttribute("student",student);
                return "student_menu";
            }
        }
        return "redirect:/authorization";
    }

    @GetMapping("/student_menu/{id:\\d+}")
    public String student_menu(Model model, HttpSession session, @PathVariable int id) {
        Student student = studentService.getById(id);
        model.addAttribute("student",student);
        return "student_menu";
    }
    @GetMapping("/student_menu/{id:\\d+}/grades")
    public String student_menu_grades(Model model, HttpSession session, @PathVariable int id) {
        Student student = studentService.getById(id);
        StudGroup group = groupService.getByNumber(student.getGroupnumber());
        List<Schedule> schedules = scheduleService.getByGroup(group.getGr_num());
        ArrayList<String> subjects = new ArrayList<>();
        ArrayList<Double> averages = new ArrayList<>();
        ArrayList<Long> skips = new ArrayList<>();
        for (Schedule s:schedules){
            double average = scheduleService.getStudentScheduleAverage(student.getId(),s.getId());
            long skip = scheduleService.countStudentSkipsBySchedule(student.getId(),s.getId());
            if (!subjects.contains(s.getSubject())){
                subjects.add(s.getSubject());
                averages.add(average);
                skips.add(skip);
            } else {
                int index = subjects.indexOf(s.getSubject());
                double old_av = averages.get(index)+average;
                averages.set(index,old_av);
                long old_sk = skips.get(index)+skip;
                skips.set(index,old_sk);
            }
        }
        model.addAttribute("general_average",student.getAverage_mark());
        model.addAttribute("all_skips",student.getSkips());
        model.addAttribute("subjects",subjects);
        model.addAttribute("averages",averages);
        model.addAttribute("skips",skips);
        model.addAttribute("student",student);
        return "student_menu_grades";
    }
    @GetMapping("/student_menu/{id:\\d+}/omissions")
    public String student_menu_omissions(Model model, HttpSession session, @PathVariable int id) {
        Student student = studentService.getById(id);
        model.addAttribute("start_date_min",LocalDate.now());
        model.addAttribute("start_date_max",LocalDate.now().plusDays(1));
        model.addAttribute("student",student);
        model.addAttribute("omissions",scheduleService.getStudentOmissions(id));
        if(!scheduleService.canAdd(LocalDate.now(),student.getId())){
            model.addAttribute("cant_add",1);
        }
        return "student_menu_omissions";
    }
    @PostMapping("/student_menu/{id:\\d+}/omissions/choose_date")
    public String chooseDate(Model model, HttpSession session, @PathVariable int id, @RequestParam LocalDate date) {
        model.addAttribute("start_date_min",LocalDate.now());
        model.addAttribute("start_date_max",LocalDate.now().plusDays(1));
        model.addAttribute("end_date_min",date.plusDays(1));
        model.addAttribute("end_date_max",date.plusDays(5));
        model.addAttribute("date",date);
        Student student = studentService.getById(id);
        model.addAttribute("omissions",scheduleService.getStudentOmissions(id));
        model.addAttribute("student",student);
        return "student_menu_omissions";
    }

    @PostMapping("/student_menu/{id:\\d+}/omissions/add")
    public String createOmission(Model model, HttpSession session, @PathVariable int id,
                                 @RequestParam LocalDate start, @RequestParam LocalDate finish) {
        Student stud = studentService.getById(id);
        Omission omission = new Omission(stud.getId(),start,finish);
        scheduleService.saveOmission(omission);
        model.addAttribute("student",stud);
        if(!scheduleService.canAdd(LocalDate.now(),id)){
            model.addAttribute("cant_add",1);
        }
        model.addAttribute("omissions",scheduleService.getStudentOmissions(id));
        return "student_menu_omissions";
    }
    @PostMapping("/student_menu/{id:\\d+}/omissions/delete")
    public String deleteOmission(Model model, HttpSession session, @PathVariable int id,
                                 @RequestParam int om_id) {
        Student stud = studentService.getById(id);
        scheduleService.deleteOmission(om_id);
        model.addAttribute("student",stud);
        if(!scheduleService.canAdd(LocalDate.now(),id)){
            model.addAttribute("cant_add",1);
        }
        model.addAttribute("start_date_min",LocalDate.now());
        model.addAttribute("start_date_max",LocalDate.now().plusDays(1));
        model.addAttribute("omissions",scheduleService.getStudentOmissions(id));
        return "student_menu_omissions";
    }
}
