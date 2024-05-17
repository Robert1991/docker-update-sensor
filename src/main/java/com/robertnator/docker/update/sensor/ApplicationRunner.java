package com.robertnator.docker.update.sensor;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRunner {

    public ConfigurableApplicationContext run(String[] args) {
        return SpringApplication.run(Application.class, args);
    }
}
