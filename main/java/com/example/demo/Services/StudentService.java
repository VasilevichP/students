package com.example.demo.Services;

import com.example.demo.Entities.Student;
import com.example.demo.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final ScheduleRepository scheduleRepository;
    private final MarkRepository markRepository;
    private final SkipRepository skipRepository;
    private final OmissionRepository omissionRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository, ScheduleRepository scheduleRepository, MarkRepository markRepository, SkipRepository skipRepository, OmissionRepository omissionRepository) {
        this.studentRepository = studentRepository;
        this.scheduleRepository = scheduleRepository;
        this.markRepository = markRepository;
        this.skipRepository = skipRepository;
        this.omissionRepository = omissionRepository;
    }

    public Iterable<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public boolean doesExist(long id) {
        Optional<Student> optStud = studentRepository.findById(id);
        return optStud.isPresent();
    }

    public boolean findByName(String name) {
        return studentRepository.existsByName(name);
    }

    public boolean findByEmail(String email) {
        return studentRepository.existsByEmail(email);
    }

    public boolean findByPhone(String phone) {
        return studentRepository.existsByPhone(phone);
    }

    public boolean findByPassport(String passport) {
        return studentRepository.existsByPassport(passport);
    }

    public boolean findByLogin(String login) {
        return studentRepository.existsBylogin(login);
    }
    public Student getByLogin(String login) {
        Optional<Student> optStud = studentRepository.findByLogin(login);
        return optStud.get();
    }
    public Student getById(long id) {
        Optional<Student> optStud = studentRepository.findById(id);
        return optStud.get();
    }

    public Student getByEmail(String email) {
        Optional<Student> optStud = studentRepository.findByEmail(email);
        return optStud.get();
    }

    public Student getByName(String name) {
        Optional<Student> optStud = studentRepository.findByName(name);
        return optStud.get();
    }

    public Student getByPassport(String passport) {
        Optional<Student> optStud = studentRepository.findByPassport(passport);
        return optStud.get();
    }

    public Student getByPhone(String phone) {
        Optional<Student> optStud = studentRepository.findByPhone(phone);
        return optStud.get();
    }

    @Transactional
    public boolean deleteStudent(long id) {
        try {
            studentRepository.deleteById(id);
            markRepository.deleteAllByStudent(id);
            skipRepository.deleteAllByStudent(id);
            omissionRepository.deleteAllByStudent(id);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void deleteAllByGroup(long group) {
        List<Student> students = findByGroup(group);
        for (Student s : students) {
            deleteStudent(s.getId());
        }
    }

    public boolean addOrChangeStudent(Student student) {
        try {
            studentRepository.save(student);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ArrayList<Student> findByGroup(long group) {
        ArrayList<Student> students = (ArrayList<Student>) StreamSupport.stream(studentRepository.findAllByGroupnumber(group).spliterator(), false)
                .collect(Collectors.toList());
        return students;
    }

    public List<Student> filterByCourse(List<Student> students, int course) {
        students = students.stream().filter(s -> s.getCourse() == course).collect(Collectors.toList());
        return students;
    }

    public List<Student> filterByFaculty(List<Student> students, String faculty) {
        students = students.stream().filter(s -> s.getFaculty().equals(faculty)).collect(Collectors.toList());
        return students;
    }

    public List<Student> filterBySpecialty(List<Student> students, String spec) {
        students = students.stream().filter(s -> s.getSpecialty().equals(spec)).collect(Collectors.toList());
        return students;
    }

    public List<Student> filterByGroup(List<Student> students, long group) {
        students = students.stream().filter(s -> (s.getGroupnumber() == group)).collect(Collectors.toList());
        return students;
    }

    public List<Student> search(List<Student> students, String search) {
        students = students.stream().filter(s -> ((s.getName().toLowerCase().contains(search.toLowerCase())) || (s.getId().toString().contains(search)))).collect(Collectors.toList());
        return students;
    }
}
