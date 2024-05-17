package com.robertnator.docker.update.sensor;

import com.robertnator.docker.update.sensor.socket.UnixSocketDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class DockerUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(DockerUpdateService.class);

    @Autowired
    private DockerHubDao dockerHubDao;

    @Autowired
    private UnixSocketDao unixSocketClient;

    public String checkForUpdates() throws IOException {
        //        for (var imageInfo : dockerHubDao.getLatestTags("koenkk/zigbee2mqtt", 10)) {
        //            logger.info("Found image info {}", imageInfo);
        //        }

        logger.info("Found images: {}", unixSocketClient.get(new File("/var/run/docker.sock"), "/images/json"));
        return "Success!!";
    }
}
