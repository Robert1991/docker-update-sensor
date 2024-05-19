package com.robertnator.docker.update.sensor;

import com.robertnator.docker.update.sensor.dao.dockerhub.DockerHubDao;
import com.robertnator.docker.update.sensor.dao.socket.DockerSocketDao;
import com.robertnator.docker.update.sensor.dao.socket.UnixSocketException;
import com.robertnator.docker.update.sensor.service.json.JsonObjectMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DockerUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(DockerUpdateService.class);

    @Autowired
    private DockerHubDao dockerHubDao;

    @Autowired
    private DockerSocketDao dockerSocketDao;

    public String checkForUpdates() throws UnixSocketException, JsonObjectMappingException {
        String imageName = "koenkk/zigbee2mqtt";
        for (var imageInfo : dockerHubDao.getLatestTags(imageName, 10)) {
            logger.info("Found hub image info {}", imageInfo);
        }

        logger.info("Found local image info {}", dockerSocketDao.getImageInfo(imageName));
        return "Success!!";
    }
}
