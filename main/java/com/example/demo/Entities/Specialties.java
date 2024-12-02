package com.example.demo.Entities;

import java.util.ArrayList;

public enum Specialties {
    ICSD("ИСиТ", Faculties.CAD),
    PMS("ПМС",Faculties.CAD),
    ASPI("АСОИ", Faculties.ITC),
    IE("ПМ",Faculties.ITC),
    IS("ВС", Faculties.CSN),
    ITS("ПОИТ", Faculties.CSN),
    CSN("ВМСиС",Faculties.CSN),
    EBE("ЭЭБ",Faculties.EE),
    DM("ЦМ",Faculties.EE);


    private String title;
    private Faculties faculty;

    Specialties(String title, Faculties faculty) {
        this.title = title;
        this.faculty = faculty;
    }

    public String getTitle() {
        return title;
    }

    public Faculties getFaculty() {
        return faculty;
    }
    public static ArrayList<String> getByFaculty(Faculties f){
        ArrayList<String> specialties = new ArrayList<>();
        for (Specialties s:Specialties.values()){
            if (s.getFaculty()==f) specialties.add(s.getTitle());
        }
        return specialties;
    }
    public static Specialties getInstance(String title){
        Specialties specialty = null;
        for (Specialties s:Specialties.values()){
            if (s.getTitle().equals(title)){
                specialty=s;
                break;
            }
        }
        return specialty;
    }
}
