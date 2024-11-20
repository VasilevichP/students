package com.example.demo.Entities;

public enum Faculties {
    CAD("ФКП"),
    ITC("ФИТУ"),
    CSN("ФКСИС"),
    EE("ИЭФ");
    private String title;

    Faculties(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
    public static Faculties getInstance(String title){
        Faculties faculty = null;
        for (Faculties f:Faculties.values()){
            if (f.getTitle().equals(title)){
                faculty=f;
                break;
            }
        }
        return faculty;
    }
}
