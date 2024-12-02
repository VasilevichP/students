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
public class Mark {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int value;
    private LocalDate date;
    private Long student;
    private Long schedule;

    public Mark(int value, LocalDate date, Long student, Long schedule) {
        this.value = value;
        this.date = date;
        this.student = student;
        this.schedule = schedule;
    }
}
