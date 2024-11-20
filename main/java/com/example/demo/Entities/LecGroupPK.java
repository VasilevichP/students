package com.example.demo.Entities;

import java.io.Serializable;

public class LecGroupPK implements Serializable {
    private long lector;
    private long grp;
    public LecGroupPK(){}

    public LecGroupPK(long lector_id, long group_number) {
        this.lector = lector_id;
        this.grp = group_number;
    }
}
