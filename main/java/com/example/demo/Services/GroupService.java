package com.example.demo.Services;

import com.example.demo.Entities.Secretary;
import com.example.demo.Entities.StudGroup;
import com.example.demo.Entities.Student;
import com.example.demo.Repositories.GroupRepository;
import com.example.demo.Repositories.LectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final StudentService studentService;

    @Autowired
    public GroupService(GroupRepository groupRepository, StudentService studentService) {
        this.groupRepository = groupRepository;
        this.studentService = studentService;
    }

    public Iterable<StudGroup> getAllGroups() {
        return groupRepository.findAll();
    }

    public boolean doesExist(long number) {
        Optional<StudGroup> optGroup = groupRepository.findById(number);
        return optGroup.isPresent();
    }

    public StudGroup getByNumber(long number) {
        Optional<StudGroup> optGroup = groupRepository.findById(number);
        return optGroup.get();
    }

    public boolean addGroup(StudGroup group) {
        try {
            groupRepository.save(group);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteGroup(long number) {
        try {
            groupRepository.deleteById(number);
            studentService.deleteAllByGroup(number);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public boolean changeGroup(StudGroup group) {
        try {
            ArrayList<Student> students = studentService.findByGroup(group.getGr_num());
            for (Student s: students){
                s.setCourse(group.getCourse());
                studentService.addOrChangeStudent(s);
            }
            groupRepository.save(group);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public ArrayList<StudGroup> searchByNumber(ArrayList<StudGroup> group,int number){
        String num = Integer.toString(number);
        Stream<StudGroup> stream = group.stream();
        group = (ArrayList<StudGroup>) stream.filter(g -> Long.toString(g.getGr_num()).contains(num)).collect(Collectors.toList());
        return group;
    }

    public ArrayList<StudGroup> filterByCourse(ArrayList<StudGroup> group,int course){
        Stream<StudGroup> stream = group.stream();
        group = (ArrayList<StudGroup>) stream.filter(g -> g.getCourse()==course).collect(Collectors.toList());
        return group;
    }
    public ArrayList<StudGroup> filterByFaculty(ArrayList<StudGroup> group,String faculty){
        Stream<StudGroup> stream = group.stream();
        group = (ArrayList<StudGroup>) stream.filter(g -> g.getFaculty().equals(faculty)).collect(Collectors.toList());
        return group;
    }
    public ArrayList<StudGroup> filterBySpecialty(ArrayList<StudGroup> group,String specialty){
        Stream<StudGroup> stream = group.stream();
        group = (ArrayList<StudGroup>) stream.filter(g -> g.getSpecialty().equals(specialty)).collect(Collectors.toList());
        return group;
    }
    public ArrayList<StudGroup> filterByForm(ArrayList<StudGroup> group,int form){
        Stream<StudGroup> stream = group.stream();
        group = (ArrayList<StudGroup>) stream.filter(g -> g.getForm_of_study()==form).collect(Collectors.toList());
        return group;
    }
    public ArrayList<StudGroup> findBySpecialty(String specialty){
        ArrayList<StudGroup> groups = (ArrayList<StudGroup>) StreamSupport.stream(groupRepository.findAllBySpecialty(specialty).spliterator(), false)
                .collect(Collectors.toList());
        return groups;
    }
}
