package com.robertnator.docker.update.sensor.service.docker;

import com.robertnator.docker.update.sensor.dao.dockerhub.DockerHubDao;
import com.robertnator.docker.update.sensor.dao.socket.DockerSocketDao;
import com.robertnator.docker.update.sensor.dao.socket.UnixSocketException;
import com.robertnator.docker.update.sensor.model.DockerUpdateInfo;
import com.robertnator.docker.update.sensor.model.dockerhub.DockerHubImageInfo;
import com.robertnator.docker.update.sensor.model.socket.DockerLocalImageInfo;
import com.robertnator.docker.update.sensor.service.DockerImageUpdateCheckException;
import com.robertnator.docker.update.sensor.service.json.JsonObjectMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DockerImageUpdateCheckService {

    private static final Logger LOG = LoggerFactory.getLogger(DockerImageUpdateCheckService.class);

    @Autowired
    private DockerHubDao dockerHubDao;

    @Autowired
    private DockerSocketDao dockerSocketDao;

    public DockerUpdateInfo checkForUpdate(String imageName)
        throws UnixSocketException, JsonObjectMappingException, DockerImageUpdateCheckException {
        DockerHubImageInfo latestVersionInfo = getLatestVersionInfo(imageName,
            dockerHubDao.getLatestTags(imageName, 50));
        DockerLocalImageInfo localImageInfo = dockerSocketDao.getImageInfo(imageName);

        String latestVersionDigest = latestVersionInfo.digest();
        if (localImageInfo.getRepoDigest().contains(latestVersionDigest)) {
            LOG.info("No new update found for {}. Latest version is {} with digest {}.", imageName,
                latestVersionInfo.name(), latestVersionDigest);
            return new DockerUpdateInfo(false, imageName, latestVersionInfo.name(),
                latestVersionDigest, latestVersionInfo.last_updated());
        }
        LOG.info("Update found for {}. Latest version is {} with digest {}.", imageName,
            latestVersionInfo.name(), latestVersionDigest);
        return new DockerUpdateInfo(true, imageName, latestVersionInfo.name(),
            latestVersionDigest, latestVersionInfo.last_updated());
    }

    private DockerHubImageInfo getLatestVersionInfo(String imageName,
        List<DockerHubImageInfo> imageInfosFromDockerHub) throws DockerImageUpdateCheckException {
        DockerHubImageInfo latestImageInfo = getLatestImageInfo(imageInfosFromDockerHub);
        if (latestImageInfo == null) {
            throw new DockerImageUpdateCheckException(
                "No info about latest version could be fetched from docker hub for image '" + imageName + "'");
        }

        List<DockerHubImageInfo> imageInfosFromDockerHubCopy = new ArrayList<>(imageInfosFromDockerHub);
        imageInfosFromDockerHubCopy.remove(latestImageInfo);
        return imageInfosFromDockerHubCopy.stream()
            .filter(imageInfo -> imageInfo.digest() != null && imageInfo.digest().equals(latestImageInfo.digest()))
            .findFirst()
            .orElse(latestImageInfo);
    }

    private static DockerHubImageInfo getLatestImageInfo(List<DockerHubImageInfo> imageInfosFromDockerHub) {
        return imageInfosFromDockerHub.stream()
            .filter(imageInfo -> imageInfo.name().equals("latest"))
            .findFirst()
            .orElse(null);
    }
}
