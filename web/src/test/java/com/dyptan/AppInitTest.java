package com.dyptan;

import org.junit.Test;
import org.junit.Assert;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class AppInitTest {

    @Test
    public void contextLoads() throws Exception {
        ConfigurableApplicationContext appContext = SpringApplication.run(SpringApp.class);
        Assert.assertTrue(appContext.isActive());
    }

}
