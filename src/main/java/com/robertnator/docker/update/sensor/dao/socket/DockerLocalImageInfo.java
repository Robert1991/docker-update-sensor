package com.robertnator.docker.update.sensor.dao.socket;

import java.util.Date;
import java.util.List;

public record DockerLocalImageInfo(String Id, List<String> RepoTags, List<String> RepoDigests, Date Created) {

}
