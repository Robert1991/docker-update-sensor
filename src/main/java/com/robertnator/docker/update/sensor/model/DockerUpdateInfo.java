package com.robertnator.docker.update.sensor.model;

import java.util.Date;

public record DockerUpdateInfo(boolean updateAvailable, String imageName, String latestVersionTag,
                               String latestVersionDigest, Date updated) {

}
