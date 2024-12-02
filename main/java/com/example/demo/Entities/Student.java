package com.example.demo.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String login;
    private String password;

    private String email;
    private String name;
    private LocalDate birth_date;
    private long groupnumber;
    private int gender;
    private String phone;
    private String passport;
    private String address;
    private String country;
    private String registration_address;
    private String faculty;
    private String specialty;
    private int course;
    private int type_of_study;
    private double scholarship;
    private double average_mark;
    private int skips;
    private String photo;

    public Student(String login, String password, String email, String name,
                   LocalDate birth_date, long gr_num, int gender, String phone, String passport,
                   String address, String country, String registration_address, String faculty, String specialty,
                   int course, int type_of_study) {
        this.login = login;
        this.password = password;
        this.email = email;
        this.name = name;
        this.birth_date = birth_date;
        this.groupnumber = gr_num;
        this.gender = gender;
        this.phone = phone;
        this.passport = passport;
        this.address = address;
        this.country = country;
        this.registration_address = registration_address;
        this.faculty = faculty;
        this.specialty = specialty;
        this.course = course;
        this.type_of_study = type_of_study;
    }
}
