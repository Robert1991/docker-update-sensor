package com.robertnator.docker.update.sensor;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {

    public static ApplicationRunner runner = new ApplicationRunner();

    public static void main(String[] args) {
        runner.run(args);
    }
}
