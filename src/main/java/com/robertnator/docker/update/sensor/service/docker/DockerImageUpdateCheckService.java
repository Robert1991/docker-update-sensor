package com.robertnator.docker.update.sensor.service.docker;

import com.robertnator.docker.update.sensor.dao.dockerhub.DockerHubDao;
import com.robertnator.docker.update.sensor.dao.socket.DockerSocketDao;
import com.robertnator.docker.update.sensor.dao.socket.UnixSocketException;
import com.robertnator.docker.update.sensor.model.DockerUpdateInfo;
import com.robertnator.docker.update.sensor.model.dockerhub.DockerHubImageInfo;
import com.robertnator.docker.update.sensor.model.socket.DockerLocalImageInfo;
import com.robertnator.docker.update.sensor.service.json.JsonObjectMappingException;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Collections.singletonList;

@Service
public class DockerImageUpdateCheckService {

    private static final Logger LOG = LoggerFactory.getLogger(DockerImageUpdateCheckService.class);

    @Autowired
    private VersionTagComparisonService versionTagComparisonService;

    @Autowired
    private DockerHubDao dockerHubDao;

    @Autowired
    private DockerSocketDao dockerSocketDao;

    public DockerUpdateInfo checkForUpdate(String imageName)
        throws UnixSocketException, JsonObjectMappingException, DockerImageUpdateCheckException {
        Pair<DockerHubImageInfo, List<DockerHubImageInfo>> latestVersionInfos = getLatestVersionInfo(imageName,
            dockerHubDao.getLatestTags(imageName, 50));

        String latestVersionDigest = latestVersionInfos.getLeft().digest();
        Date latestVersionUpdate = latestVersionInfos.getLeft().last_updated();
        List<String> latestVersionTags = latestVersionInfos.getRight().stream()
            .map(DockerHubImageInfo::name)
            .toList();
        String bestSchematicVersioningTag = versionTagComparisonService.getBestSchematicVersioningTag(
            latestVersionTags);

        DockerLocalImageInfo localImageInfo = dockerSocketDao.getImageInfo(imageName);
        if (localImageInfo.getRepoDigest().contains(latestVersionDigest)) {
            LOG.info("No new update found for {}. Latest version is {} with digest {}.", imageName,
                bestSchematicVersioningTag, latestVersionDigest);
            return new DockerUpdateInfo(false, imageName, bestSchematicVersioningTag, latestVersionTags,
                latestVersionDigest, latestVersionUpdate);
        }
        LOG.info("Update found for {}. Latest version is {} with digest {}.", imageName, bestSchematicVersioningTag,
            latestVersionDigest);
        return new DockerUpdateInfo(true, imageName, bestSchematicVersioningTag, latestVersionTags,
            latestVersionDigest, latestVersionUpdate);
    }

    private Pair<DockerHubImageInfo, List<DockerHubImageInfo>> getLatestVersionInfo(String imageName,
        List<DockerHubImageInfo> imageInfosFromDockerHub) throws DockerImageUpdateCheckException {
        DockerHubImageInfo latestImageInfo = getLatestImageInfo(imageInfosFromDockerHub);
        if (latestImageInfo == null) {
            throw new DockerImageUpdateCheckException(
                "No info about latest version could be fetched from docker hub for image '" + imageName + "'");
        }

        List<DockerHubImageInfo> imageInfosFromDockerHubCopy = new ArrayList<>(imageInfosFromDockerHub);
        imageInfosFromDockerHubCopy.remove(latestImageInfo);
        List<DockerHubImageInfo> latestImageInfos = imageInfosFromDockerHubCopy.stream()
            .filter(imageInfo -> imageInfo.digest() != null && imageInfo.digest().equals(latestImageInfo.digest()))
            .toList();
        if (latestImageInfos.isEmpty()) {
            return Pair.of(latestImageInfo, singletonList(latestImageInfo));
        }
        return Pair.of(latestImageInfo, latestImageInfos);
    }

    private static DockerHubImageInfo getLatestImageInfo(List<DockerHubImageInfo> imageInfosFromDockerHub) {
        return imageInfosFromDockerHub.stream()
            .filter(imageInfo -> imageInfo.name().equals("latest"))
            .findFirst()
            .orElse(null);
    }
}
