package com.dyptan.controller;

import com.dyptan.model.Car;
import com.dyptan.ModelTrainer;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;


@RestController
public class StreamingController {

    private final ReactiveMongoTemplate mongoTemplate;
    private final ModelTrainer trainer = new ModelTrainer();

    public StreamingController(ReactiveMongoTemplate mongoTemplate) throws IOException {
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping(
            value = {"/mongostream"},
            produces = {"text/event-stream"}
    )
    public Flux<Car> olxCars() {
        return this.mongoTemplate.tail(new Query(), Car.class).share();
    }

    @GetMapping("/trainModel")
    public void train(){
        trainer.train();
    }

    @GetMapping("/saveModel")
    public void save(){
        trainer.save();
    }

}