package com.robertnator.docker.update.sensor;

import com.robertnator.docker.update.sensor.socket.UnixSocketException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/check")
public class DockerUpdateController {

    @Autowired
    private DockerUpdateService dockerUpdateService;

    @GetMapping("/updates")
    public String checkForUpdates() throws UnixSocketException {
        try {
            return dockerUpdateService.checkForUpdates();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
