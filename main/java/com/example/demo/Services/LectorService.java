package com.example.demo.Services;

import com.example.demo.Entities.*;
import com.example.demo.Repositories.LecGroupRepository;
import com.example.demo.Repositories.LecSubjRepository;
import com.example.demo.Repositories.LectorRepository;
import com.example.demo.Repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LectorService{
    private final LectorRepository lectorRepository;
    private final LecSubjRepository lecSubjRepository;
    private final LecGroupRepository lecGroupRepository;
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public LectorService(LectorRepository lectorRepository, LecSubjRepository lecSubjRepository, LecGroupRepository lecGroupRepository, ScheduleRepository scheduleRepository) {
        this.lectorRepository = lectorRepository;
        this.lecSubjRepository = lecSubjRepository;
        this.lecGroupRepository = lecGroupRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public Iterable<Lector> getAllLectors (){
        return lectorRepository.findAll();
    }

    public boolean existsByName(String name){
        return lectorRepository.existsByName(name);
    }
    public boolean existsByEmail(String email){
        return lectorRepository.existsByEmail(email);
    }
    public boolean existsByLogin(String login) {return lectorRepository.existsByLogin(login);}
    public Lector getByLogin(String login) {
        Optional<Lector> lector = lectorRepository.findByLogin(login);
        return lector.get();
    }
    public Lector getByEmail(String email){
        Optional<Lector> lector = lectorRepository.findByEmail(email);
        return lector.get();
    }
    public Lector getByName(String name) {
        Optional<Lector> optStud = lectorRepository.findByName(name);
        return optStud.get();
    }
    public Lector getById(long id){
        Optional<Lector> lector = lectorRepository.findById(id);
        return  lector.get();
    }
    public boolean addLector(Lector lector){
        try {
            lectorRepository.save(lector);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    @Transactional
    public boolean saveLector(Lector lector){
        try {
            lectorRepository.save(lector);
            lecGroupRepository.deleteAllByLector(lector.getId());
            lecSubjRepository.deleteAllByLector(lector.getId());
            return true;
        }catch (Exception e){
            System.out.println();
            return false;
        }
    }
    public void updateSchedules(Lector lector){
        ArrayList<Long> groups = getGroups(lector.getId());
        ArrayList<String> subjects = getSubjects(lector.getId());
        List<Schedule> schedules = (List<Schedule>) scheduleRepository.getSchedulesByLector(lector.getId());
        for (Schedule s:schedules){
            if((!groups.contains(s.getGroupnumber()))||(!subjects.contains(s.getSubject()))) scheduleRepository.delete(s);
        }
    }
    public boolean addGroups(long lector_id,long group_number){
        try{
            LecGroup lecGroup = new LecGroup(lector_id, group_number);
            lecGroupRepository.save(lecGroup);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public ArrayList<Long> getGroups(long id){
        Iterable<LecGroup> lecGroups = lecGroupRepository.getLecGroupsByLector(id);
        ArrayList<Long> groups = new ArrayList<>();
        for(LecGroup l:lecGroups){
            groups.add(l.getGrp());
        }
        return groups;
    }
    public ArrayList<Lector> getBySubject(String subject){
        Iterable<LecSubj> lecSubjs = lecSubjRepository.getLecSubjsBySubject(subject);
        ArrayList<Lector> lectors = new ArrayList<>();
        for (LecSubj l:lecSubjs){
            try {
                Lector lector = getById(l.getLector());
                lectors.add(lector);
            }catch (Exception e){}
        }
        return lectors;
    }
    public ArrayList<Lector> getByGroup(long group){
        Iterable<LecGroup> lecGroups = lecGroupRepository.getLecGroupsByGrp(group);
        ArrayList<Lector> lectors = new ArrayList<>();
        for (LecGroup l:lecGroups){
            try {
                Lector lector = getById(l.getLector());
                lectors.add(lector);
            }catch (Exception e){}
        }
        return lectors;
    }
    public String getStringGroups(long id){
        ArrayList<Long> gr_nums = getGroups(id);
        StringBuilder stringBuilder = new StringBuilder();
        for (Long l: gr_nums) {
            stringBuilder.append(l + ", ");
        }
        String groups = stringBuilder.toString().substring(0,stringBuilder.length()-2);
        System.out.println(groups);
        return groups;
    }
    public boolean addSubjects(long lector_id,String subj){
        try{
            LecSubj lecSubj = new LecSubj(lector_id, subj);
            lecSubjRepository.save(lecSubj);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public boolean deleteLector(long lector_id){
        try{
            lectorRepository.deleteById(lector_id);
            lecSubjRepository.deleteAllByLector(lector_id);
            lecGroupRepository.deleteAllByLector(lector_id);
            scheduleRepository.deleteAllByLector(lector_id);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public ArrayList<String> getSubjects(long id){
        Iterable<LecSubj> lecSubjs = lecSubjRepository.getLecSubjsByLector(id);
        ArrayList<String> subjects = new ArrayList<>();
        for(LecSubj l:lecSubjs){
            subjects.add(l.getSubject());
        }
        return subjects;
    }
    public String getStringSubjects(long id){
        ArrayList<String> subjects = getSubjects(id);
        StringBuilder stringBuilder = new StringBuilder();
        for (String s: subjects) {
            stringBuilder.append(s + ", ");
        }
        String subs = stringBuilder.toString().substring(0,stringBuilder.length()-2);
        System.out.println(subs);
        return subs;
    }
    public List<Lector> search(List<Lector> lectors, String search) {
        lectors = lectors.stream().filter(s -> ((s.getName().toLowerCase().contains(search.toLowerCase())) || (s.getId().toString().contains(search)))).collect(Collectors.toList());
        return lectors;
    }
}
