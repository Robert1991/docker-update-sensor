package com.robertnator.docker.update.sensor.dao.socket;

import com.robertnator.docker.update.sensor.model.socket.DockerLocalImageInfo;
import com.robertnator.docker.update.sensor.service.json.JsonObjectMappingException;
import com.robertnator.docker.update.sensor.service.json.JsonObjectMappingService;
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

    public DockerLocalImageInfo getImageInfo(String imageName) throws UnixSocketException, JsonObjectMappingException {
        String imageInfo = unixSocketDao.get(new File(DOCKER_UNIX_SOCKET), String.format("/images/%s/json", imageName));
        return jsonObjectMappingService.mapToClass(imageInfo, DockerLocalImageInfo.class, true);
    }
}
