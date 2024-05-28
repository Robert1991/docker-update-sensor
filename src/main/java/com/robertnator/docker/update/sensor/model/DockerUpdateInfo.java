package com.robertnator.docker.update.sensor.model;

import java.util.Date;
import java.util.List;

public record DockerUpdateInfo(boolean updateAvailable, String imageName, String latestVersionTag,
                               List<String> latestVersionTags, String latestVersionDigest, Date updated) {

}
