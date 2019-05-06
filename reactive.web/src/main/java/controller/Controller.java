package controller;

//import ua
import model.Model;
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
    public Flux<Model> olxCars() {
        return this.mongoTemplate.tail(new Query(), Model.class).share();
    }

    @PostMapping("/trainModel")
    public void train(){



    }

}