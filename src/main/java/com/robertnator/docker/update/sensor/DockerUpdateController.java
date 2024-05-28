package com.robertnator.docker.update.sensor;

import com.robertnator.docker.update.sensor.dao.socket.UnixSocketException;
import com.robertnator.docker.update.sensor.model.DockerUpdateInfo;
import com.robertnator.docker.update.sensor.service.docker.DockerImageUpdateCheckException;
import com.robertnator.docker.update.sensor.service.docker.DockerImageUpdateCheckService;
import com.robertnator.docker.update.sensor.service.json.JsonObjectMappingException;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/check")
@Validated
public class DockerUpdateController {

    @Autowired
    private DockerImageUpdateCheckService dockerImageUpdateCheckService;

    @GetMapping("/update")
    public DockerUpdateInfo checkForUpdates(
        @RequestParam(name = "image")
        @NotBlank(message = "image name cannot be empty")
        String image) throws UnixSocketException, JsonObjectMappingException,
        DockerImageUpdateCheckException {
        return dockerImageUpdateCheckService.checkForUpdate(image);
    }
}
