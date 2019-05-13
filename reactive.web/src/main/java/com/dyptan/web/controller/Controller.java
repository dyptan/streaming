package com.dyptan.web.controller;

import com.dyptan.web.model.Car;
import com.dyptan.streaming.spark.ModelTrainer;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
public class Controller {
    private final ReactiveMongoTemplate mongoTemplate;

    public Controller(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping(
            value = {"/mongostream"},
            produces = {"text/event-stream"}
    )
    public Flux<Car> olxCars() {
        return this.mongoTemplate.tail(new Query(), Car.class).share();
    }

    @PostMapping("/trainModel")
    public void train(){

        ModelTrainer trainer = new ModelTrainer();
                trainer.train();
        trainer.save("here");

    }

}