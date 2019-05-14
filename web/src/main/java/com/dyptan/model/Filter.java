package com.dyptan.model;

import com.dyptan.util.Period;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.*;
import java.util.Arrays;

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
}
