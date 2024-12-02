package com.example.demo.Controllers;

import com.example.demo.Entities.*;
import com.example.demo.Services.GroupService;
import com.example.demo.Services.LectorService;
import com.example.demo.Services.ScheduleService;
import com.example.demo.Services.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Controller
public class LectorController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private LectorService lectorService;
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/lector_menu")
    public String lector_main(Model model, HttpSession session) {
        String user = (String) session.getAttribute("user");
        if (user != null && !user.isEmpty()) {
            if (lectorService.existsByLogin(user)) {
                Lector lector = lectorService.getByLogin(user);
                List<Long> groups = lectorService.getGroups(lector.getId());
                List<String> subjects = lectorService.getSubjects(lector.getId());
                model.addAttribute("groups", groups);
                session.setAttribute("lector", lector);
                model.addAttribute("subjects", subjects);
                return "lector_menu";
            }
        }
        return "redirect:/authorization";
    }

    @PostMapping("/lector_menu/choose")
    public String choose(Model model, HttpSession session, @RequestParam Optional<Integer> group, @RequestParam String subject) {
        List<Long> groups = new ArrayList<>();
        List<String> subjects = new ArrayList<>();
        List<Schedule> schedules = new ArrayList<>();
        Lector lector = (Lector) session.getAttribute("lector");
        if (group.isEmpty() && subject.isEmpty()) {
            groups = lectorService.getGroups(lector.getId());
            subjects = lectorService.getSubjects(lector.getId());
        }
        if (group.isPresent() && subject.isEmpty()) {
            schedules = scheduleService.getByLectorAndGroup(lector.getId(), group.get());
            groups = lectorService.getGroups(lector.getId());
            subjects = scheduleService.getDistSubjectsBySch(schedules);
        }
        if (group.isEmpty() && !subject.isEmpty()) {
            schedules = scheduleService.getByLectorAndSubject(lector.getId(), subject);
            subjects = lectorService.getSubjects(lector.getId());
            groups = scheduleService.getDistGroupsBySch(schedules);
        }
        if (group.isPresent() && !subject.isEmpty()) {
            schedules = scheduleService.getByLectorAndGroupAndSubject(lector.getId(), group.get(), subject);
            subjects = scheduleService.getDistSubjectsBySch(schedules);
            groups = scheduleService.getDistGroupsBySch(schedules);
            int month = LocalDate.now().getMonthValue();
            int year = LocalDate.now().getYear();
            LocalDate startDate = null;
            if (month >= 9 && month <= 12) startDate = LocalDate.of(year, 9, 1);
            if (month >= 2 && month <= 5) startDate = LocalDate.of(year, 2, 1);
            int day = startDate.getDayOfWeek().getValue();
            HashSet<LocalDate> dates = new HashSet<>();
            List<Omission> omissions = scheduleService.getGroupOmissions(group.get());
            Map<Long,LocalDate> om_map = new HashMap<>();
            for (Schedule s : schedules) {
                LocalDate scheduleStartDate = startDate.plusDays(s.getDay() < day ? 7 - day + s.getDay() : s.getDay() - day);
                for (LocalDate d = scheduleStartDate; d.isBefore(startDate.plusDays(110)); d = d.plusDays(7)) {
                    for (Omission o:omissions){
                        if (!(d.isBefore(o.getBegin())||d.isAfter(o.getEnd()))){
                            om_map.put(o.getStudent(),d);break;
                        }
                    }
                    dates.add(d);
                }
            }
            List<LocalDate> sortedDates = new ArrayList<>(dates);
            Collections.sort(sortedDates);
            Iterable<Student> students = studentService.findByGroup(group.get());
            List<Mark> marks = scheduleService.getMarksBySchedule(schedules);
            List<Skip> skips = scheduleService.getSkipsBySchedule(schedules);
            List<Double> averages = new ArrayList<>();
            for(Student s:students){
                averages.add(scheduleService.getStudentSchedulesAverage(s.getId(),schedules));
            }
            model.addAttribute("omissions",om_map);
            model.addAttribute("averages",averages);
            model.addAttribute("marks",marks);
            model.addAttribute("skips",skips);
            model.addAttribute("schedule_dates", sortedDates);
            model.addAttribute("students", students);
        }
        model.addAttribute("groups", groups);
        model.addAttribute("subjects", subjects);
        if (group.isPresent())
            model.addAttribute("group", group.get());
        model.addAttribute("subject", subject);
        return "lector_menu";
    }

    @PostMapping("/lector_menu/mark")
    public String mark(Model model, HttpSession session, @RequestParam int group, @RequestParam String subject,
                       @RequestParam int student, @RequestParam LocalDate date, @RequestParam String value) {
        Lector lector = (Lector) session.getAttribute("lector");
        Schedule schedule = scheduleService.getSpecificSchedule(lector.getId(),group,subject);
        if(value.equals("N")){
            Skip skip = new Skip(date, (long) student,schedule.getId());
            scheduleService.saveSkip(skip);
        }
        else {
            Mark mark = new Mark(Integer.parseInt(value),date, (long) student,schedule.getId());
            scheduleService.saveMark(mark);
        }
        scheduleService.setStudentSkips(student);
        scheduleService.setStudentAverageMark(student);
        List<Long> groups = new ArrayList<>();
        List<String> subjects = new ArrayList<>();
        List<Schedule> schedules = new ArrayList<>();
        schedules = scheduleService.getByLectorAndGroupAndSubject(lector.getId(), group, subject);
        subjects = scheduleService.getDistSubjectsBySch(schedules);
        groups = scheduleService.getDistGroupsBySch(schedules);
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();
        LocalDate startDate = null;
        if (month >= 9 && month <= 12) startDate = LocalDate.of(year, 9, 1);
        if (month >= 2 && month <= 5) startDate = LocalDate.of(year, 2, 1);
        int day = startDate.getDayOfWeek().getValue();
        HashSet<LocalDate> dates = new HashSet<>();
        List<Omission> omissions = scheduleService.getGroupOmissions(group);
        Map<Long,LocalDate> om_map = new HashMap<>();
        for (Schedule s : schedules) {
            LocalDate scheduleStartDate = startDate.plusDays(s.getDay() < day ? 7 - day + s.getDay() : s.getDay() - day);
            for (LocalDate d = scheduleStartDate; d.isBefore(startDate.plusDays(110)); d = d.plusDays(7)) {
                for (Omission o:omissions){
                    if (!(d.isBefore(o.getBegin())||d.isAfter(o.getEnd()))){
                        om_map.put(o.getStudent(),d);break;
                    }
                }
                dates.add(d);
            }
        }
        List<LocalDate> sortedDates = new ArrayList<>(dates);
        Collections.sort(sortedDates);
        Iterable<Student> students = studentService.findByGroup(group);
        List<Mark> marks = scheduleService.getMarksBySchedule(schedules);
        List<Skip> skips = scheduleService.getSkipsBySchedule(schedules);
        List<Double> averages = new ArrayList<>();
        for(Student s:students){
            averages.add(scheduleService.getStudentSchedulesAverage(s.getId(),schedules));
        }
        model.addAttribute("omissions",om_map);
        model.addAttribute("averages",averages);
        model.addAttribute("marks",marks);
        model.addAttribute("skips",skips);
        model.addAttribute("schedule_dates", sortedDates);
        model.addAttribute("students", students);
        model.addAttribute("groups", groups);
        model.addAttribute("subjects", subjects);
        model.addAttribute("group", group);
        model.addAttribute("subject", subject);
        return "lector_menu";
    }

    @PostMapping("/lector_menu/change")
    public String change(Model model, HttpSession session,@RequestParam int group, @RequestParam String subject,
                         @RequestParam int id, @RequestParam String old_value,@RequestParam String new_value){
        System.out.println(id);
        if(old_value.equals("H") && !new_value.equals("N")){
            Skip skip = scheduleService.getSkipById(id);
            long student = skip.getStudent();
            long sched = skip.getSchedule();
            LocalDate date = skip.getDate();
            Mark m = new Mark(Integer.parseInt(new_value),date,student,sched);
            scheduleService.saveMark(m);
            scheduleService.deleteSkip(id);
            scheduleService.setStudentSkips(student);
            scheduleService.setStudentAverageMark(student);
        }
        if(!old_value.equals("H") && new_value.equals("N")){
            Mark mark = scheduleService.getMarkById(id);
            long student = mark.getStudent();
            long sched = mark.getSchedule();
            LocalDate date = mark.getDate();
            Skip s = new Skip(date,student,sched);
            scheduleService.saveSkip(s);
            scheduleService.deleteMark(id);
            scheduleService.setStudentSkips(student);
            scheduleService.setStudentAverageMark(student);
        }
        if(!old_value.equals("H") && !new_value.equals("N")){
            Mark mark = scheduleService.getMarkById(id);
            long student = mark.getStudent();
            mark.setValue(Integer.parseInt(new_value));
            scheduleService.saveMark(mark);
            scheduleService.setStudentAverageMark(student);
        }
        Lector lector = (Lector) session.getAttribute("lector");
        Schedule schedule = scheduleService.getSpecificSchedule(lector.getId(),group,subject);
        List<Long> groups = new ArrayList<>();
        List<String> subjects = new ArrayList<>();
        List<Schedule> schedules = new ArrayList<>();
        schedules = scheduleService.getByLectorAndGroupAndSubject(lector.getId(), group, subject);
        subjects = scheduleService.getDistSubjectsBySch(schedules);
        groups = scheduleService.getDistGroupsBySch(schedules);
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();
        LocalDate startDate = null;
        if (month >= 9 && month <= 12) startDate = LocalDate.of(year, 9, 1);
        if (month >= 2 && month <= 5) startDate = LocalDate.of(year, 2, 1);
        int day = startDate.getDayOfWeek().getValue();
        HashSet<LocalDate> dates = new HashSet<>();
        List<Omission> omissions = scheduleService.getGroupOmissions(group);
        Map<Long,LocalDate> om_map = new HashMap<>();
        for (Schedule s : schedules) {
            LocalDate scheduleStartDate = startDate.plusDays(s.getDay() < day ? 7 - day + s.getDay() : s.getDay() - day);
            for (LocalDate d = scheduleStartDate; d.isBefore(startDate.plusDays(110)); d = d.plusDays(7)) {
                for (Omission o:omissions){
                    if (!(d.isBefore(o.getBegin())||d.isAfter(o.getEnd()))){
                        om_map.put(o.getStudent(),d);break;
                    }
                }
                dates.add(d);
            }
        }
        List<LocalDate> sortedDates = new ArrayList<>(dates);
        Collections.sort(sortedDates);
        Iterable<Student> students = studentService.findByGroup(group);
        List<Mark> marks = scheduleService.getMarksBySchedule(schedules);
        List<Skip> skips = scheduleService.getSkipsBySchedule(schedules);
        List<Double> averages = new ArrayList<>();
        for(Student s:students){
            averages.add(scheduleService.getStudentSchedulesAverage(s.getId(),schedules));
        }
        model.addAttribute("omissions",om_map);
        model.addAttribute("averages",averages);
        model.addAttribute("marks",marks);
        model.addAttribute("skips",skips);
        model.addAttribute("schedule_dates", sortedDates);
        model.addAttribute("students", students);
        model.addAttribute("groups", groups);
        model.addAttribute("subjects", subjects);
        model.addAttribute("group", group);
        model.addAttribute("subject", subject);
        return "lector_menu";
    }

    @PostMapping("/lector_menu/delete")
    public String delete(Model model, HttpSession session,@RequestParam int group, @RequestParam String subject,
                         @RequestParam String id, @RequestParam String value){
        if (value.equals("H")){
            long stud = scheduleService.getSkipById(Long.parseLong(id)).getStudent();
            scheduleService.deleteSkip(Long.parseLong(id));
            scheduleService.setStudentSkips(stud);
        }else {
            long stud = scheduleService.getMarkById(Long.parseLong(id)).getStudent();
            scheduleService.deleteMark(Long.parseLong(id));
            scheduleService.setStudentAverageMark(stud);
        }
        System.out.println(id);
        Lector lector = (Lector) session.getAttribute("lector");
        Schedule schedule = scheduleService.getSpecificSchedule(lector.getId(),group,subject);
        List<Long> groups = new ArrayList<>();
        List<String> subjects = new ArrayList<>();
        List<Schedule> schedules = new ArrayList<>();
        schedules = scheduleService.getByLectorAndGroupAndSubject(lector.getId(), group, subject);
        subjects = scheduleService.getDistSubjectsBySch(schedules);
        groups = scheduleService.getDistGroupsBySch(schedules);
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();
        LocalDate startDate = null;
        if (month >= 9 && month <= 12) startDate = LocalDate.of(year, 9, 1);
        if (month >= 2 && month <= 5) startDate = LocalDate.of(year, 2, 1);
        int day = startDate.getDayOfWeek().getValue();
        HashSet<LocalDate> dates = new HashSet<>();
        List<Omission> omissions = scheduleService.getGroupOmissions(group);
        Map<Long,LocalDate> om_map = new HashMap<>();
        for (Schedule s : schedules) {
            LocalDate scheduleStartDate = startDate.plusDays(s.getDay() < day ? 7 - day + s.getDay() : s.getDay() - day);
            for (LocalDate d = scheduleStartDate; d.isBefore(startDate.plusDays(110)); d = d.plusDays(7)) {
                for (Omission o:omissions){
                    if (!(d.isBefore(o.getBegin())||d.isAfter(o.getEnd()))){
                        om_map.put(o.getStudent(),d);break;
                    }
                }
                dates.add(d);
            }
        }
        List<LocalDate> sortedDates = new ArrayList<>(dates);
        Collections.sort(sortedDates);
        Iterable<Student> students = studentService.findByGroup(group);
        List<Mark> marks = scheduleService.getMarksBySchedule(schedules);
        List<Skip> skips = scheduleService.getSkipsBySchedule(schedules);
        List<Double> averages = new ArrayList<>();
        for(Student s:students){
            averages.add(scheduleService.getStudentSchedulesAverage(s.getId(),schedules));
        }
        model.addAttribute("omissions",om_map);
        model.addAttribute("averages",averages);
        model.addAttribute("marks",marks);
        model.addAttribute("skips",skips);
        model.addAttribute("schedule_dates", sortedDates);
        model.addAttribute("students", students);
        model.addAttribute("groups", groups);
        model.addAttribute("subjects", subjects);
        model.addAttribute("group", group);
        model.addAttribute("subject", subject);
        return "lector_menu";
    }
}