package com.example.demo.Services;

import com.example.demo.Entities.Student;
import com.example.demo.Repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
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

    public boolean deleteStudent(long id) {
        try {
            studentRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public void deleteAllByGroup(long group) {
        studentRepository.deleteAllByGroupnumber(group);
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
}
