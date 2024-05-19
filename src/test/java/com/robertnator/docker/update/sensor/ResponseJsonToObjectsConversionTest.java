package com.robertnator.docker.update.sensor;

import com.robertnator.docker.update.sensor.model.dockerhub.DockerHubImageInfo;
import com.robertnator.docker.update.sensor.service.json.JsonObjectMappingException;
import com.robertnator.docker.update.sensor.service.json.JsonObjectMappingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Date;

import static com.robertnator.docker.update.sensor.utils.TestResourceUtils.getTestResourcePath;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

@ExtendWith(MockitoExtension.class)
public class ResponseJsonToObjectsConversionTest {

    @InjectMocks
    private JsonObjectMappingService jsonObjectMappingService;

    @Test
    void testReadDockerHubImageInfo(TestInfo testInfo) throws IOException, JsonObjectMappingException {
        String sampleJsonString = Files.readString(getTestResourcePath(testInfo));

        DockerHubImageInfo[] imageInfos = jsonObjectMappingService.mapToClass(sampleJsonString,
            DockerHubImageInfo[].class);
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
}
