package com.dyptan.model;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(
    collection = "cars"
)
@Getter
public class Car {
    private String model;
    private Double price_usd;
    private Double race_km;
    private Double year;
    private String category;
    private Double engine_cubic_cm;
    private Double prediction;
}
