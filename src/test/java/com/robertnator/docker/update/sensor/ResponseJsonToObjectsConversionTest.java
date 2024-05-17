package com.robertnator.docker.update.sensor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

@SpringBootTest
public class ResponseJsonToObjectsConversionTest {

    @Autowired
    private JsonObjectMappingService jsonObjectMappingService;

    @Test
    void testReadLocalDockerImageInfos(TestInfo testInfo) throws IOException {
        String sampleJsonString = Files.readString(getTestResourcePath(testInfo));

        LocalDockerImageInfo[] imageInfos = jsonObjectMappingService.mapToClass(sampleJsonString, LocalDockerImageInfo[].class);
        assertThat(asList(imageInfos), contains(
                        new LocalDockerImageInfo("sha256:1c1331b1fddc2625f82a4d4daa27c98e52062ade06a9e0a282703e583dafa93b",
                                List.of("homeassistant/home-assistant:latest"),
                                List.of("homeassistant/home-assistant@sha256:dfe252fdd57c929e678e741690e97099a38f9adff8ae87a4a7972a7b8ed61a32"),
                                new Date(1715018122)),
                        new LocalDockerImageInfo("sha256:7482af2336fd8e10484dd2b2385e6b9f6cc7a57ba7bbf93299ef4fd546ab06f9",
                                List.of("esphome/esphome:latest"),
                                List.of("esphome/esphome@sha256:e208d1e7ca3c7e66aa1eb0b5eb3081a09f8e2b9a34704bf6c00c02c1a70e4576"),
                                new Date(1713830022)),
                        new LocalDockerImageInfo("sha256:051cb67876a609e838c4be62bf88348ba896b8411d17b3221743a1d31466a114",
                                List.of("prom/prometheus:latest"),
                                List.of("prom/prometheus@sha256:4f6c47e39a9064028766e8c95890ed15690c30f00c4ba14e7ce6ae1ded0295b1"),
                                new Date(1712759409))
                )
        );
    }

    @Test
    void testReadDockerHubImageInfo(TestInfo testInfo) throws IOException {
        String sampleJsonString = Files.readString(getTestResourcePath(testInfo));

        DockerHubImageInfo[] imageInfos = jsonObjectMappingService.mapToClass(sampleJsonString, DockerHubImageInfo[].class);
        assertThat(asList(imageInfos), contains(
                new DockerHubImageInfo("504323559", toDate("2024-05-09T20:33:26.875021Z"), "latest",
                        "sha256:d37f2f8227d9c2763ddbe06e48328f9ec3a9b8cf081dfcbda44c9b0af4c0c634"),
                new DockerHubImageInfo("663061833", toDate("2024-05-09T20:33:32.321107Z"), "1.37.1",
                        "sha256:d37f2f8227d9c2763ddbe06e48328f9ec3a9b8cf081dfcbda44c9b0af4c0c634"),
                new DockerHubImageInfo("654743174", toDate("2024-05-01T19:45:03.994863Z"), "1.37.0",
                        "sha256:7032615b5ac807c805854fb3390e9e84e1efc732893918c3686cbb5d2c75c2bc")
        ));
    }

    private static Date toDate(String dateString) {
        return new Date(Instant.parse(dateString).toEpochMilli());
    }

    private Path getTestResourcePath(TestInfo testInfo) {
        return Path.of("src/test/resources", testInfo.getTestClass().get().getSimpleName(),
                testInfo.getTestMethod().get().getName() + ".json");
    }

    private <T> T mapJsonObjectToClass(Class<T> classOfT, String jsonString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonString, classOfT);
    }

    private record LocalDockerImageInfo(String Id, List<String> RepoTags, List<String> RepoDigests, Date Created) {
    }
}
