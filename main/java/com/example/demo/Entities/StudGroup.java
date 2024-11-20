package com.example.demo.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StudGroup {
    @Id
    private long gr_num;
    private String specialty;
    private int course;
    private String faculty;
    private int form_of_study;
}
