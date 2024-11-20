package com.example.demo.Services;

import com.example.demo.Entities.Schedule;
import com.example.demo.Entities.SchedulePK;
import com.example.demo.Repositories.LectorRepository;
import com.example.demo.Repositories.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final LectorService lectorService;

    public ScheduleService(ScheduleRepository scheduleRepository, LectorService lectorService) {
        this.scheduleRepository = scheduleRepository;
        this.lectorService = lectorService;
    }
    public boolean doesExist(SchedulePK schedule){
        Optional<Schedule> sch = scheduleRepository.findById(schedule);
        return sch.isPresent();
    }
    public long findHowManyForGroup(long group, int day){
        return scheduleRepository.countByGroupnumberAndDay(group, day);
    }
    public long findHowManyForLector(long lector, int day){
        return scheduleRepository.countByLectorAndDay(lector, day);
    }
    public List<Map<String, Object>> getByLector(long lector){
        List<Schedule> schedules = (List<Schedule>) scheduleRepository.getSchedulesByLector(lector);
        List<Map<String, Object>> maps = new ArrayList<>();
        for (Schedule s:schedules){
            Map<String, Object> map = new HashMap<>();
            map.put("day", DayOfWeek.of(s.getDay()).getDisplayName(TextStyle.FULL, Locale.forLanguageTag("ru")));
            map.put("group",s.getGroupnumber());
            map.put("lector", lectorService.getById(lector).getName());
            map.put("subject",s.getSubject());
            maps.add(map);
        }
        return maps;
    }
    public boolean addSchedule(Schedule schedule){
        try {
            scheduleRepository.save(schedule);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
