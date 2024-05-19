package com.robertnator.docker.update.sensor.dao.socket;

import com.robertnator.docker.update.sensor.dao.json.JsonObjectMappingException;
import com.robertnator.docker.update.sensor.dao.json.JsonObjectMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DockerSocketDao {

    public static String DOCKER_UNIX_SOCKET = "/var/run/docker.sock";

    @Autowired
    private UnixSocketDao unixSocketDao;

    @Autowired
    private JsonObjectMappingService jsonObjectMappingService;

    public DockerLocalImageInfo getImageInfo(String imageName) throws UnixSocketException, JsonProcessingException {
        String imagesInfo = unixSocketDao.get(new File(DOCKER_UNIX_SOCKET),
            String.format("/images/%s/json", imageName));
        return jsonObjectMappingService.mapToClass(imagesInfo, DockerLocalImageInfo.class);
    }
}
