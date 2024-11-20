package com.example.demo.Entities;

import java.io.Serializable;

public class LecSubjPK implements Serializable {
    private long lector;
    private String subject;

    public LecSubjPK(long lector_id, String subject_name) {
        this.lector = lector_id;
        this.subject = subject_name;
    }

    public LecSubjPK() {
    }
}
