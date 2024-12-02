package com.example.demo.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Omission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long student;
    private LocalDate begin;
    private LocalDate end;
    private int status;

    public Omission(Long student, LocalDate begin, LocalDate end) {
        this.student = student;
        this.begin = begin;
        this.end = end;
        this.status = 0;
    }
}
