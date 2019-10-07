package com.dyptan.model;

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
    private String published;

    public String getModel() {
        return this.model;
    }

    public Double getPrice_usd() {
        return this.price_usd;
    }

    public Double getRace_km() {
        return this.race_km;
    }

    public Double getYear() {
        return this.year;
    }

    public String getCategory() {
        return this.category;
    }

    public Double getEngine_cubic_cm() {
        return this.engine_cubic_cm;
    }

    public Double getPrediction() {
        return this.prediction;
    }

    public String getPublished() {
        return this.published;
    }
}
