package com.robertnator.docker.update.sensor;

import com.robertnator.docker.update.sensor.dao.json.JsonObjectMappingException;
import com.robertnator.docker.update.sensor.dao.socket.UnixSocketException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/check")
public class DockerUpdateController {

    @Autowired
    private DockerUpdateService dockerUpdateService;

    @GetMapping("/updates")
    public String checkForUpdates() throws UnixSocketException, JsonObjectMappingException {
        return dockerUpdateService.checkForUpdates();
    }
}
