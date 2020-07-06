package com.dyptan;

import org.junit.Test;
import org.junit.Assert;
// import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.context.ConfigurableApplicationContext;

public class AppInitTest {

    @Test
    public void contextLoads() throws Exception {
        ConfigurableApplicationContext appContext = SpringApplication.run(SpringApp.class);
        Assert.assertTrue(appContext.isActive());
    }

}
