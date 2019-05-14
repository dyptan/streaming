package com.dyptan.util;

public enum Period {
    DAYS ("d"),
    WEEKS("w"),
    MONTHS("M");

    private final String abbreviation;

    Period(String abbreviation){
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {return abbreviation;}
}
