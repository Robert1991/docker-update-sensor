package com.robertnator.docker.update.sensor.model;

import java.util.Date;
import java.util.List;

public record DockerUpdateInfo(boolean updateAvailable,
                               String imageName,
                               String latestVersionTag,
                               List<String> latestVersionTags,
                               String latestVersionDigest,
                               String currentVersionTag,
                               List<String> currentVersionTags,
                               String currentVersionDigest,
                               Date updated) {

    public static DockerUpdateInfo updateAvailable(String imageName,
        String latestVersionTag,
        List<String> latestVersionTags,
        String latestVersionDigest,
        String currentVersionTag,
        List<String> currentVersionTags,
        String currentVersionDigest,
        Date updated) {
        return new DockerUpdateInfo(true, imageName, latestVersionTag, latestVersionTags, latestVersionDigest,
            currentVersionTag, currentVersionTags, currentVersionDigest, updated);
    }

    public static DockerUpdateInfo noUpdate(String imageName,
        String latestVersionTag,
        List<String> latestVersionTags,
        String latestVersionDigest,
        String localImageDigest,
        Date updated) {
        return new DockerUpdateInfo(false, imageName, latestVersionTag, latestVersionTags, latestVersionDigest,
            latestVersionTag, latestVersionTags, localImageDigest, updated);
    }

}
