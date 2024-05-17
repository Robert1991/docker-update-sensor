package com.robertnator.docker.update.sensor;

import java.util.Date;

public record DockerHubImageInfo(String id, Date last_updated, String name, String digest) {
}