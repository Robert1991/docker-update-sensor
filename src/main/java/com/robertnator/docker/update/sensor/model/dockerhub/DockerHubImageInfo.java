package com.robertnator.docker.update.sensor.model.dockerhub;

import java.util.Date;

public record DockerHubImageInfo(String id, Date last_updated, String name, String digest) {

}