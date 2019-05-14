package com.dyptan.web.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(
    collection = "cars"
)
public class Car {
    private String model;
    private Double price_usd;
    private Double race_km;
    private Double year;
    private String category;
    private Double engine_cubic_cm;
    private Double prediction;
}
