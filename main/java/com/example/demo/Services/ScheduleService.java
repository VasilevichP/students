package com.example.demo.Services;

import com.example.demo.Entities.*;
import com.example.demo.Repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final LectorService lectorService;
    private final MarkRepository markRepository;
    private final SkipRepository skipRepository;
    private final StudentService studentService;
    private final OmissionRepository omissionRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, LectorService lectorService, MarkRepository markRepository, SkipRepository skipRepository, StudentService studentService, OmissionRepository omissionRepository) {
        this.scheduleRepository = scheduleRepository;
        this.lectorService = lectorService;
        this.markRepository = markRepository;
        this.skipRepository = skipRepository;
        this.studentService = studentService;
        this.omissionRepository = omissionRepository;
    }

    public long findHowManyForGroup(long group, int day) {
        return scheduleRepository.countByGroupnumberAndDay(group, day);
    }

    public long findHowManyForLector(long lector, int day) {
        return scheduleRepository.countByLectorAndDay(lector, day);
    }

    public List<Schedule> getByGroup(long group) {
        return (List<Schedule>) scheduleRepository.getSchedulesByGroupnumber(group);
    }

    public List<Map<String, Object>> getByLector(long id, int val) {
        List<Schedule> schedules = new ArrayList<>();
        if (val == 1)
            schedules = (List<Schedule>) scheduleRepository.getSchedulesByLector(id);
        if (val == 2) schedules = (List<Schedule>) scheduleRepository.getSchedulesByGroupnumber(id);
        List<Map<String, Object>> maps = new ArrayList<>();
        int iter = 0;
        for (DayOfWeek d : DayOfWeek.values()) {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("day", d.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("ru")));
            List<Map<String, Object>> schedule = new ArrayList<>();
            int rows = 0;
            Map<String, Object> map;
            for (Schedule s : schedules) {
                if (s.getDay() == d.getValue()) {
                    map = new HashMap<>();
                    map.put("group", s.getGroupnumber());
                    map.put("lector", lectorService.getById(s.getLector()).getName());
                    map.put("subject", s.getSubject());
                    map.put("day", s.getDay());
                    map.put("id",s.getId());
                    schedule.add(map);
                    rows++;
                }
            }
            if (rows < 7) {
                while (rows < 7) {
                    map = new HashMap<>();
                    map.put("group", "");
                    map.put("lector", "");
                    map.put("subject", "");
                    schedule.add(map);
                    rows++;
                }
            }
            map1.put("schedules", schedule);
            iter++;
            maps.add(map1);
            if (iter == 6) break;
        }
        return maps;
    }

    public List<Schedule> getByLectorAndSubject(long lector, String subject) {
        return (List<Schedule>) scheduleRepository.getSchedulesByLectorAndSubject(lector, subject);
    }

    public List<Schedule> getByLectorAndGroup(long lector, long group) {
        return (List<Schedule>) scheduleRepository.getSchedulesByLectorAndGroupnumber(lector, group);
    }

    public List<Schedule> getByLectorAndGroupAndSubject(long lector, long group, String subject) {
        return (List<Schedule>) scheduleRepository.getSchedulesByLectorAndGroupnumberAndSubject(lector, group, subject);
    }

    public Schedule getSpecificSchedule(long lector, long group, String subject) {
        List<Schedule> schedules = getByLectorAndGroupAndSubject(lector, group, subject);
        return schedules.get(0);
    }

    public boolean addSchedule(Schedule schedule) {
        try {
            scheduleRepository.save(schedule);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    @Transactional
    public void deleteScheduleByID(long id){
        try {
            scheduleRepository.deleteAllById(id);
            deleteMarkBySchedule(id);
            deleteSkipBySchedule(id);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public List<String> getDistSubjectsBySch(List<Schedule> schedules) {
        Set<String> subjs = new HashSet<>();
        for (Schedule s : schedules) {
            subjs.add(s.getSubject());
        }
        ArrayList<String> subjects = new ArrayList<>(subjs);
        return subjects;
    }

    public List<Long> getDistGroupsBySch(List<Schedule> schedules) {
        Set<Long> subjs = new HashSet<>();
        for (Schedule s : schedules) {
            subjs.add(s.getGroupnumber());
        }
        ArrayList<Long> subjects = new ArrayList<>(subjs);
        return subjects;
    }

    public void saveMark(Mark mark) {
        try {
            markRepository.save(mark);
        } catch (Exception e) {
        }
    }

    public void saveSkip(Skip skip) {
        try {
            skipRepository.save(skip);
        } catch (Exception e) {
        }
    }

    public Mark getMarkById(long id) {
        Optional<Mark> mark = markRepository.findById(id);
        return mark.get();
    }

    public Skip getSkipById(long id) {
        Optional<Skip> skip = skipRepository.findById(id);
        return skip.get();
    }

    public void deleteSkip(long id) {
        try {
            skipRepository.deleteById(id);
        } catch (Exception e) {
        }
    }

    public void deleteMark(long id) {
        try {
            markRepository.deleteById(id);
        } catch (Exception e) {
        }
    }

    public List<Mark> getMarksBySchedule(List<Schedule> schedules) {
        List<Mark> marks = new ArrayList<>();
        for (Schedule s : schedules) {
            Iterable<Mark> marks1 = markRepository.findAllBySchedule(s.getId());
            marks.addAll((Collection<? extends Mark>) marks1);
        }
        return marks;
    }

    public void setStudentSkips(long student) {
        long skips = skipRepository.countAllByStudent(student);
        Student stud = studentService.getById(student);
        stud.setSkips((int) skips);
        studentService.addOrChangeStudent(stud);
    }

    public long countStudentSkipsBySchedule(long student, long schedule) {
        return skipRepository.countAllByStudentAndSchedule(student, schedule);
    }

    public void setStudentAverageMark(long student) {
        List<Mark> marks = (List<Mark>) markRepository.findAllByStudent(student);
        if (!marks.isEmpty()) {
            int all = 0;
            for (Mark m : marks) {
                all += m.getValue();
            }
            double average = (double) all / marks.size();
            double rounded = (double) (Math.round((average) * 100.0)) / 100;
            Student stud = studentService.getById(student);
            stud.setAverage_mark(rounded);
            studentService.addOrChangeStudent(stud);
        }
    }

    public double getStudentSchedulesAverage(long student, List<Schedule> schedules) {
        List<Mark> marks = new ArrayList<>();
        for (Schedule s : schedules) {
            Iterable<Mark> marks1 = markRepository.findAllByStudentAndSchedule(student, s.getId());
            marks.addAll((Collection<? extends Mark>) marks1);
        }
        double result = 0;
        if (!marks.isEmpty()) {
            int all = 0;
            for (Mark m : marks) {
                all += m.getValue();
            }
            double average = (double) all / marks.size();
            result = (double) (Math.round((average) * 100.0)) / 100;

        }
        return result;
    }

    public double getStudentScheduleAverage(long student, long schedule) {
        List<Mark> marks = (List<Mark>) markRepository.findAllByStudentAndSchedule(student, schedule);
        double result = 0;
        if (!marks.isEmpty()) {
            int all = 0;
            for (Mark m : marks) {
                all += m.getValue();
            }
            double average = (double) all / marks.size();
            result = (double) (Math.round((average) * 100.0)) / 100;

        }
        return result;
    }

    public List<Skip> getSkipsBySchedule(List<Schedule> schedules) {
        List<Skip> skips = new ArrayList<>();
        for (Schedule s : schedules) {
            Iterable<Skip> skips1 = skipRepository.findAllByScheduleAndLegimateFalse(s.getId());
            skips.addAll((Collection<? extends Skip>) skips1);
        }
        return skips;
    }

    public void deleteMarkByStudent(long student) {
        try {
            markRepository.deleteAllByStudent(student);
        } catch (Exception e) {
        }
    }

    public void deleteSkipByStudent(long student) {
        try {
            skipRepository.deleteAllByStudent(student);
        } catch (Exception e) {
        }
    }

    public void deleteMarkBySchedule(long schedule) {
        try {
            markRepository.deleteAllBySchedule(schedule);
        } catch (Exception e) {
        }
    }

    public void deleteSkipBySchedule(long schedule) {
        try {
            skipRepository.deleteAllBySchedule(schedule);
        } catch (Exception e) {
        }
    }
    public List<Omission> getOmissions() {
        return (List<Omission>) omissionRepository.findAll();
    }
    public List<Omission> getOmissionsByStatus(int status) {
        return (List<Omission>) omissionRepository.findAllByStatus(status);
    }
    public List<Omission> getStudentOmissions(long student) {
        return (List<Omission>) omissionRepository.findAllByStudent(student);
    }

    public void saveOmission(Omission omission) {
        try {
            omissionRepository.save(omission);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void approveOmission(Omission omission) {
        saveOmission(omission);
        long student = omission.getStudent();
        List<Skip> skips = (List<Skip>) skipRepository.findAllByStudent(student);
        for (Skip s:skips){
            if (!(s.getDate().isBefore(omission.getBegin())||s.getDate().isAfter(omission.getEnd()))){
                s.setLegimate(true);
                saveSkip(s);
            }
        }
    }
    public Omission getOmissionById(long id){
        return omissionRepository.findById(id).get();
    }
    public boolean canAdd(LocalDate date, long student){
        boolean canAdd = true;
        int year = date.getYear();
        int month = date.getMonthValue();
        List<Omission> omissions = getStudentOmissions(student);
        for (Omission omission:omissions){
            if(omission.getBegin().getMonthValue() == month && omission.getBegin().getYear() == year && omission.getStatus()!=2){
                canAdd = false;
                break;
            }
        }
        return canAdd;
    };
    public void deleteOmission(long id){
        try {
            omissionRepository.deleteById(id);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    public List<Omission> getGroupOmissions(long group){
        List<Student> students = studentService.findByGroup(group);
        List<Omission> omissions = new ArrayList<>();
        for(Student student: students){
            List<Omission> oms = (List<Omission>) omissionRepository.findAllByStudentAndStatus(student.getId(),1);
            omissions.addAll(oms);
        }
        return omissions;
    }
}
