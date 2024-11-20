package com.example.demo.Controllers;

import com.example.demo.Entities.Lector;
import com.example.demo.Entities.Schedule;
import com.example.demo.Entities.StudGroup;
import com.example.demo.Entities.Subject;
import com.example.demo.Repositories.ScheduleRepository;
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

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.*;

@Controller
public class SecretaryScheduleController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private LectorService lectorService;
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/secretary_schedule_menu")
    public String secretary_schedule_menu(Model model, HttpSession httpSession) {
        Character user = (Character) httpSession.getAttribute("user");
        if (user != null) {
            if (user == 's') {
                return "secretary_schedule";
            }
        }
        return "redirect:/authorization";
    }

    @PostMapping("/secretary_schedule_menu/group_or_lector")
    public String groupOrLector(Model model, HttpSession httpSession, @RequestParam int val) {
        if (val == 1) {
            Iterable<Lector> lectors = lectorService.getAllLectors();
            ArrayList<String> subjects = new ArrayList<>();
            for (Subject sub : Subject.values()) {
                subjects.add(sub.getTitle());
            }
            model.addAttribute("lectors", lectors);
            model.addAttribute("subjects", subjects);
            model.addAttribute("lec_sch", 1);
        }
        if (val == 2) {
            Iterable<StudGroup> groups = groupService.getAllGroups();
            model.addAttribute("groups", groups);
            model.addAttribute("gr_sch", 1);
        }
        return "secretary_schedule";
    }

    @PostMapping("/secretary_schedule_menu/lec_sch_choose")
    public String choose(Model model, HttpSession httpSession, @RequestParam Optional<Integer> lector, @RequestParam String subject) {
        List<Lector> lectors = new ArrayList<>();
        List<String> subjects = new ArrayList<>();
        if (lector.isPresent()) {
            model.addAttribute("lector_chosen", lectorService.getById(lector.get()).getName());
            ArrayList<String> days = new ArrayList<>();
            int iter = 0;
            for (DayOfWeek d : DayOfWeek.values()) {
                days.add(d.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("ru")));
                iter++;
                if (iter == 6) break;
            }
            List<Map<String, Object>> schedule = scheduleService.getByLector(lector.get());
            model.addAttribute("schedules",schedule);
            model.addAttribute("days", days);
        }
        if (lector.isPresent() && subject.isEmpty()) {
            model.addAttribute("lector", lector.get());
            subjects = lectorService.getSubjects(lector.get());
            lectors = (List<Lector>) lectorService.getAllLectors();
        }
        if (lector.isEmpty() && !subject.isEmpty()) {
            model.addAttribute("subject", subject);
            for (Subject sub : Subject.values()) {
                subjects.add(sub.getTitle());
            }
            lectors = lectorService.getBySubject(subject);
        }
        if (lector.isPresent() && !subject.isEmpty()) {
            model.addAttribute("lector", lector.get());
            model.addAttribute("subject", subject);
            lectors = lectorService.getBySubject(subject);
            subjects = lectorService.getSubjects(lector.get());
            model.addAttribute("canAdd", 1);
            Lector lec = lectorService.getById(lector.get());
            ArrayList<Long> groups = lectorService.getGroups(lec.getId());
            model.addAttribute("groups", groups);
        }
        if (lector.isEmpty() && subject.isEmpty()) {
            lectors = (List<Lector>) lectorService.getAllLectors();
            for (Subject sub : Subject.values()) {
                subjects.add(sub.getTitle());
            }
        }
        model.addAttribute("lectors", lectors);
        model.addAttribute("subjects", subjects);
        model.addAttribute("subject", subject);
        model.addAttribute("lec_sch", 1);
        return "secretary_schedule";
    }

    @PostMapping("/secretary_schedule_menu/gr_sch_choose_group")
    public String chooseGroup(Model model, HttpSession httpSession, @RequestParam Optional<Integer> group) {
        Iterable<StudGroup> groups = groupService.getAllGroups();
        model.addAttribute("groups", groups);
        model.addAttribute("gr_sch", 1);
        if (group.isPresent()) {
            ArrayList<String> days = new ArrayList<>();
            int iter = 0;
            for (DayOfWeek d : DayOfWeek.values()) {
                days.add(d.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("ru")));
                iter++;
                if (iter == 6) break;
            }
            model.addAttribute("days", days);
            model.addAttribute("group", group.get());
            List<Lector> lectors = lectorService.getByGroup((long) group.get());
            if (!lectors.isEmpty()) {
                Lector lec = lectors.get(0);
                List<String> subjects = lectorService.getSubjects(lec.getId());
                model.addAttribute("gr_lector", lec.getId());
                model.addAttribute("subjects", subjects);
            }
            model.addAttribute("lectors", lectors);
        }
        return "secretary_schedule";
    }

    @PostMapping("/secretary_schedule_menu/gr_sch_choose_lector")
    public String chooseGroupLector(Model model, HttpSession httpSession, @RequestParam int group, @RequestParam int lector) {
        Iterable<StudGroup> groups = groupService.getAllGroups();
        model.addAttribute("groups", groups);
        model.addAttribute("gr_sch", 1);
        model.addAttribute("group", group);
        Iterable<Lector> lectors = lectorService.getByGroup(group);
        model.addAttribute("lectors", lectors);
        model.addAttribute("gr_lector", lector);
        Lector lec = lectorService.getById(lector);
        List<String> subjects = lectorService.getSubjects(lec.getId());
        model.addAttribute("subjects", subjects);
        ArrayList<String> days = new ArrayList<>();
        int iter = 0;
        for (DayOfWeek d : DayOfWeek.values()) {
            days.add(d.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("ru")));
            iter++;
            if (iter == 6) break;
        }
        model.addAttribute("days", days);
        return "secretary_schedule";
    }

    @PostMapping("/secretary_schedule_menu/add")
    public String addSchedule(Model model, HttpSession httpSession, @RequestParam int lector, @RequestParam String subject,
                              @RequestParam int group, @RequestParam int day, @RequestParam int val) {
        ArrayList<String> days = new ArrayList<>();
        int iter = 0;
        for (DayOfWeek d : DayOfWeek.values()) {
            days.add(d.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("ru")));
            iter++;
            if (iter == 6) break;
        }
        model.addAttribute("days", days);
        model.addAttribute("day", day);
        model.addAttribute("group", group);
        model.addAttribute("subject", subject);
        if (val == 1) {
            ArrayList<Lector> lectors = lectorService.getBySubject(subject);
            ArrayList<String> subjects = lectorService.getSubjects(lector);
            model.addAttribute("lector_chosen", lectorService.getById(lector).getName());
            model.addAttribute("lector", lector);
            model.addAttribute("lec_sch", 1);
            model.addAttribute("canAdd", 1);
            model.addAttribute("lectors", lectors);
            model.addAttribute("subjects", subjects);
            ArrayList<Long> groups = lectorService.getGroups(lector);
            model.addAttribute("groups", groups);
        }
        if (val == 2) {
            List<Lector> lectors = lectorService.getByGroup((long) group);
            if (!lectors.isEmpty()) {
                List<String> subjects = lectorService.getSubjects(lector);
                model.addAttribute("gr_lector", lector);
                model.addAttribute("subjects", subjects);
                model.addAttribute("lectors", lectors);
                Iterable<StudGroup> groups = groupService.getAllGroups();
                model.addAttribute("groups", groups);
                model.addAttribute("gr_sch", 1);
            }
        }
        int forLector = (int) scheduleService.findHowManyForLector(lector, day);
        int forGroup = (int) scheduleService.findHowManyForGroup(group, day);
        System.out.println("fl" + forLector);
        System.out.println("fg" + forGroup);
        if (forLector < 7 && forGroup < 7) {
            Schedule schedule = new Schedule(lector, group, subject, day);
            scheduleService.addSchedule(schedule);
        }
        return secretary_schedule_menu(model, httpSession);
    }
}
