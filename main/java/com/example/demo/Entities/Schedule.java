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
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Schedule(long lector, long groupnumber, String subject, int day) {
        this.lector = lector;
        this.groupnumber = groupnumber;
        this.subject = subject;
        this.day = day;
    }
}
