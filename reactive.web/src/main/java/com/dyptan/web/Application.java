package com.dyptan.web;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com/dyptan/web/controller"})
@SpringBootApplication
public class Application {
    public Application() {
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
