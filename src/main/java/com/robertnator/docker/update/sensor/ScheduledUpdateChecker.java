package com.robertnator.docker.update.sensor;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledUpdateChecker {

    private final DockerUpdateService dockerUpdateService;

    public ScheduledUpdateChecker(DockerUpdateService dockerUpdateService) {
        this.dockerUpdateService = dockerUpdateService;
    }

    @Scheduled(fixedRate = 3600000) // every hour
    public void checkUpdatesRegularly() {
        //System.out.println(dockerUpdateService.checkForUpdates());
    }
}