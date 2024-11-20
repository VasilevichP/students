package com.example.demo.Services;

import com.example.demo.Entities.Lector;
import com.example.demo.Entities.Secretary;
import com.example.demo.Entities.Student;
import com.example.demo.Repositories.LectorRepository;
import com.example.demo.Repositories.SecretaryRepositary;
import com.example.demo.Repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Optional;

@Service
public class AuthRegService {
    private SecretaryRepositary secretaryRepositary;
    private StudentRepository studentRepository;
    private LectorRepository lectorRepository;

    @Autowired
    public AuthRegService(SecretaryRepositary secretaryRepositary, StudentRepository studentRepository, LectorRepository lectorRepository) {
        this.secretaryRepositary = secretaryRepositary;
        this.lectorRepository = lectorRepository;
        this.studentRepository = studentRepository;
    }

    public Object authorize(String login, String password) {
        try {
            Optional<Secretary> optSec = secretaryRepositary.findById(login);
            if (optSec.isPresent()) {
                if (optSec.get().getPassword().equals(password) && optSec.get().getStatus() == 1)
                    return optSec.get();
            }
            Optional<Lector> optLec = lectorRepository.findByLogin(login);
            if (optLec.isPresent()) {
                if (optSec.get().getPassword().equals(password))
                    return optLec.get();
            }
            Optional<Student> optStud = studentRepository.findByLogin(login);
            if (optStud.isPresent()) {
                if (optStud.get().getPassword().equals(password))
                    return optStud.get();
            }
        } catch (Exception e) {
            return "Неправильный логин или пароль";
        }
        return "Неправильный логин или пароль";
    }

    public int register(String login, String password, int role, String email) {
        switch (role) {
            case 0:
                if (!studentRepository.existsByEmail(email)) return 8;
                else {
                    if (studentRepository.findByEmail(email).get().getPassword().equals("")) {
                        if (studentRepository.existsBylogin(login) || lectorRepository.existsByLogin(login) || secretaryRepositary.existsById(login))
                            return 5;
                        else {
                            Student stud = studentRepository.findByEmail(email).get();
                            stud.setLogin(login);
                            stud.setPassword(password);
                            studentRepository.save(stud);
                            return 0;
                        }
                    } else {
                        System.out.println(studentRepository.findByEmail(email).get().getPassword());
                        return 9;
                    }
                }
            case 1:
                if (!lectorRepository.existsByEmail(email)) return 8;
                else {
                    if (lectorRepository.findByEmail(email).get().getPassword() == null) {
                        if (studentRepository.existsBylogin(login) || lectorRepository.existsByLogin(login) || secretaryRepositary.existsById(login))
                            return 5;
                        else {
                            Lector lec = lectorRepository.findByEmail(email).get();
                            lec.setPassword(password);
                            lectorRepository.save(lec);
                            return 0;
                        }
                    }
                    return 9;
                }
            case 2:
                if (secretaryRepositary.existsById(login)) return 5;
                else {
                    Secretary sec = new Secretary(login, password, 0);
                    if (secretaryRepositary.count() == 0) sec.setStatus(1);
                    secretaryRepositary.save(sec);
                    return 0;
                }
            default:
                return 7;
        }
    }
}
