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
import java.util.function.Predicate;

import static com.robertnator.docker.update.sensor.model.DockerUpdateInfo.noUpdate;
import static com.robertnator.docker.update.sensor.model.DockerUpdateInfo.updateAvailable;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

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
        List<DockerHubImageInfo> dockerHubTagInfos = dockerHubDao.getTags(imageName, 100);
        Pair<DockerHubImageInfo, List<DockerHubImageInfo>> latestVersionInfos = getLatestVersionInfo(imageName,
            dockerHubTagInfos);

        String latestVersionDigest = latestVersionInfos.getLeft().digest();
        Date latestVersionUpdate = latestVersionInfos.getLeft().last_updated();
        List<String> latestVersionTags = getImageNames(latestVersionInfos.getRight());
        String latestVersionTag = versionTagComparisonService.getBestSchematicVersioningTag(latestVersionTags);

        DockerLocalImageInfo localImageInfo = dockerSocketDao.getImageInfo(imageName);
        String localImageDigest = localImageInfo.getRepoDigest();
        if (localImageDigest.contains(latestVersionDigest)) {
            LOG.info("No new update found for {}. Latest version is {} with digest {}.", imageName,
                latestVersionTag, latestVersionDigest);
            return noUpdate(imageName, latestVersionTag, latestVersionTags, latestVersionDigest, localImageDigest,
                latestVersionUpdate);
        }
        LOG.info("Update found for {}. Latest version is {} with digest {}.", imageName, latestVersionTag,
            latestVersionDigest);
        List<String> currentImageInfos = getImageNames(
            getImageInfosForDigest(dockerHubTagInfos, localImageDigest::contains));
        String currentVersionTag = versionTagComparisonService.getBestSchematicVersioningTag(currentImageInfos);
        return updateAvailable(imageName, latestVersionTag, latestVersionTags, latestVersionDigest, currentVersionTag,
            currentImageInfos, localImageDigest, latestVersionUpdate);
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
        List<DockerHubImageInfo> latestImageInfos = getImageInfosForDigest(imageInfosFromDockerHubCopy,
            digest -> digest.equals(latestImageInfo.digest()));
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

    private static List<DockerHubImageInfo> getImageInfosForDigest(List<DockerHubImageInfo> imageInfosFromDockerHubCopy,
        Predicate<String> digestMatchingFunction) {
        return imageInfosFromDockerHubCopy.stream()
            .filter(imageInfo -> isNotEmpty(imageInfo.digest()) && digestMatchingFunction.test(imageInfo.digest()))
            .toList();
    }

    private static List<String> getImageNames(List<DockerHubImageInfo> dockerHubTagInfos) {
        return dockerHubTagInfos.stream()
            .map(DockerHubImageInfo::name)
            .toList();
    }
}
