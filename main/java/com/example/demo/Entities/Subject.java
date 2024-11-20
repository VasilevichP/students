package com.example.demo.Entities;

public enum Subject {
    NAP("ПСП"),
    PE("Физкультура"),
    TTADIS("СиТАиРИС"),
    EAP("ПКП"),
    PS("Политология"),
    PMIS("ПМИС"),
    NTDWA("СТР-web"),
    CA("АВС"),
    BA("БА"),
    SDT("ТРПО"),
    TDCIS("ТПСИС"),
    MA("Мат. анализ"),
    CH("КЧ");

    private String title;

    Subject(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
    public static Subject getInstance(String title){
        Subject subject = null;
        for (Subject s:Subject.values()){
            if (s.getTitle().equals(title)){
                subject=s;
                break;
            }
        }
        return subject;
    }
}
