package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(SchedulePK.class)
public class Schedule {
    @Id
    private long lector;
    @Id
    private long groupnumber;
    @Id
    private String subject;
    @Id
    private int day;
}
