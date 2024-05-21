package com.robertnator.docker.update.sensor.model.socket;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Date;
import java.util.List;

public record DockerLocalImageInfo(String Id,
                                   @JsonDeserialize(using = EnsureSingletonListDeserializer.class)
                                   List<String> RepoTags,
                                   @JsonDeserialize(using = EnsureSingletonListDeserializer.class)
                                   List<String> RepoDigests,
                                   Date Created) {

    public String getRepoTag() {
        return RepoTags.getFirst();
    }

    public String getRepoDigest() {
        return RepoDigests.getFirst();
    }
}
