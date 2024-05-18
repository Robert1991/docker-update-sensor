package com.robertnator.docker.update.sensor;

import com.robertnator.docker.update.sensor.socket.DockerSocketDao;
import com.robertnator.docker.update.sensor.socket.UnixSocketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DockerUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(DockerUpdateService.class);

    @Autowired
    private DockerHubDao dockerHubDao;

    @Autowired
    private DockerSocketDao dockerSocketDao;

    public String checkForUpdates() throws IOException, UnixSocketException {
        for (var imageInfo : dockerHubDao.getLatestTags("koenkk/zigbee2mqtt", 10)) {
            logger.info("Found hub image info {}", imageInfo);
        }

        for (var localImageInfo : dockerSocketDao.getImages()) {
            logger.info("Found local image info {}", localImageInfo);
        }

        return "Success!!";
    }
}
