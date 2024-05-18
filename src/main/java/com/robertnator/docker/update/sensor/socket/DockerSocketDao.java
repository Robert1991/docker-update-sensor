package com.robertnator.docker.update.sensor.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.robertnator.docker.update.sensor.DockerLocalImageInfo;
import com.robertnator.docker.update.sensor.JsonObjectMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

import static java.util.Arrays.asList;

@Component
public class DockerSocketDao {

    public static String DOCKER_UNIX_SOCKET = "/var/run/docker.sock";

    @Autowired
    private UnixSocketDao unixSocketDao;

    @Autowired
    private JsonObjectMappingService jsonObjectMappingService;

    public List<DockerLocalImageInfo> getImages() throws UnixSocketException, JsonProcessingException {
        String imagesInfo = unixSocketDao.get(new File("/var/run/docker.sock"), "/images/json");
        return asList(jsonObjectMappingService.mapToClass(imagesInfo, DockerLocalImageInfo[].class));
    }
}
