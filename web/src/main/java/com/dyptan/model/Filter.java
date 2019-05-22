package com.dyptan.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.Table;
import javax.persistence.Transient;

@Data
@Embeddable
@Table(name = "Filters")
public class Filter {

    private @Transient short limit = 100;
    private String models ;
    private String brands ;
    private @Transient Period periodMultiplier ;
    private @Transient int periodRange ;
    private short yearFrom ;
    private short yearTo ;
    private int priceFrom;
    private int priceTo ;

    @Override
    public String toString(){
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }

    public enum Period{
        DAYS ("d"),
        WEEKS("w"),
        MONTHS("M");

        private final String abbreviation;

        Period(String abbreviation){
            this.abbreviation = abbreviation;
        }

        public String getAbbreviation() {return abbreviation;}
    }
}


