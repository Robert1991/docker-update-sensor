package com.robertnator.docker.update.sensor.dao.dockerhub;


import com.robertnator.docker.update.sensor.model.dockerhub.DockerHubImageInfo;
import com.robertnator.docker.update.sensor.service.json.JsonObjectMappingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.robertnator.docker.update.sensor.dao.dockerhub.DockerHubDao.DOCKER_API_URL;
import static com.robertnator.docker.update.sensor.utils.DateUtils.toDate;
import static com.robertnator.docker.update.sensor.utils.TestResourceUtils.getTestResourceContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
public class DockerHubDaoIntegrationTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private DockerHubDao daoUnderTest;

    @Test
    void testGetTags(TestInfo testInfo) throws JsonObjectMappingException {
        when(restTemplate.getForObject(DOCKER_API_URL + "/repo/someImage/tags?page_size=3", String.class))
            .thenReturn(getTestResourceContent(testInfo, "Page1", ".json"));
        when(restTemplate.getForObject(DOCKER_API_URL + "/repo/someImage/tags?page=2&page_size=3", String.class))
            .thenReturn(getTestResourceContent(testInfo, "Page2", ".json"));

        List<DockerHubImageInfo> actualTags = daoUnderTest.getTags("repo/someImage", 3);

        assertThat(actualTags)
            .contains(
                new DockerHubImageInfo("2669311", toDate("2024-05-24T20:44:26.118861Z"), "latest",
                    "sha256:031d355a2e52e82fc33cd6854753fb19fc5d6a31af6f0f54c277d6f118ad993e"),
                new DockerHubImageInfo("688391408", toDate("2024-06-05T02:43:18.442821Z"), "fooo",
                    "sha256:c3ca3e6fb2eebf990abcf1c6dbc0241083b7abc51b68e8dc6db2c3a456b88deb"),
                new DockerHubImageInfo("2669404", toDate("2024-06-05T02:43:14.82111Z"), "dev",
                    "sha256:d3fc40fe2a240f3f7409b748b427d7d0ca8a3384314916f5574751c0a7251122"),
                new DockerHubImageInfo("674091793", toDate("2024-05-22T02:35:47.96512Z"), "1.0",
                    "sha256:7b71427d39c7b6f8e48068c6abda49dde21448a2bb0d6176a769453a8b9bb4d2"),
                new DockerHubImageInfo("674091779", toDate("2024-05-22T02:35:06.387125Z"), "0.9",
                    "sha256:28788eb9eec3531081e0018942c1931d0c17736f7027aa25129d65cee0cb0fea")
            );
    }
}
