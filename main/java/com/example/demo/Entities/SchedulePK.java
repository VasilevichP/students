package com.example.demo.Entities;

import java.io.Serializable;

public class SchedulePK implements Serializable {
    private long lector;
    private long groupnumber;
    private String subject;
    private int day;
    public SchedulePK(){}

    public SchedulePK(long lector, long groupnumber, String subject, int day) {
        this.lector = lector;
        this.groupnumber = groupnumber;
        this.subject = subject;
        this.day = day;
    }
}
