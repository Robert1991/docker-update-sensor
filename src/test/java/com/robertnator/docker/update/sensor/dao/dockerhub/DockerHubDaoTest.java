package com.robertnator.docker.update.sensor.dao.dockerhub;


import com.robertnator.docker.update.sensor.model.dockerhub.DockerHubImageInfo;
import com.robertnator.docker.update.sensor.service.json.JsonObjectMappingException;
import com.robertnator.docker.update.sensor.service.json.JsonObjectMappingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

import static com.robertnator.docker.update.sensor.dao.dockerhub.DockerHubDao.DOCKER_API_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DockerHubDaoTest {

    @Mock
    private JsonObjectMappingService jsonObjectMappingService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DockerHubDao daoUnderTest;

    @BeforeEach
    void setUp() throws JsonObjectMappingException {
        when(jsonObjectMappingService.mapToClass("[\"response from docker hub\"]", DockerHubImageInfo[].class, false))
            .thenReturn(new DockerHubImageInfo[]{new DockerHubImageInfo("id", new Date(123), "latest", "digest")});
    }

    @Test
    void testGetTags() throws JsonObjectMappingException {
        String expectedApiQuery = DOCKER_API_URL + "/repository/name" + "/tags?page_size=" + 10;
        when(restTemplate.getForObject(expectedApiQuery, String.class))
            .thenReturn("{ \"results\": [\"response from docker hub\"] }");

        List<DockerHubImageInfo> imageInfos = daoUnderTest.getTags("repository/name", 10);

        assertThat(imageInfos).contains(new DockerHubImageInfo("id", new Date(123), "latest", "digest"));
    }

    @Test
    void testGetTagsForDockerContainerWithVersion() throws JsonObjectMappingException {
        String expectedApiQuery = DOCKER_API_URL + "/repository/name" + "/tags?page_size=" + 10;
        when(restTemplate.getForObject(expectedApiQuery, String.class))
            .thenReturn("{ \"results\": [\"response from docker hub\"] }");

        List<DockerHubImageInfo> imageInfos = daoUnderTest.getTags("repository/name:version", 10);

        assertThat(imageInfos).contains(new DockerHubImageInfo("id", new Date(123), "latest", "digest"));
    }

    @Test
    void testGetTagsWithDockerHubRepositoryAndVersion() throws JsonObjectMappingException {
        String expectedApiQuery = DOCKER_API_URL + "/library/imageName" + "/tags?page_size=" + 10;
        when(restTemplate.getForObject(expectedApiQuery, String.class)).thenReturn(
            "{ \"results\": [\"response from docker hub\"] }");

        List<DockerHubImageInfo> imageInfos = daoUnderTest.getTags("imageName:10", 10);

        assertThat(imageInfos).contains(new DockerHubImageInfo("id", new Date(123), "latest", "digest"));
    }

    @Test
    void testGetTagsWithDockerHubRepository() throws JsonObjectMappingException {
        String expectedApiQuery = DOCKER_API_URL + "/library/imageName" + "/tags?page_size=" + 10;
        when(restTemplate.getForObject(expectedApiQuery, String.class)).thenReturn(
            "{ \"results\": [\"response from docker hub\"] }");

        List<DockerHubImageInfo> imageInfos = daoUnderTest.getTags("imageName", 10);

        assertThat(imageInfos).contains(new DockerHubImageInfo("id", new Date(123), "latest", "digest"));
    }

    @Test
    void testGetTagsWithMultiplePages() throws JsonObjectMappingException {
        String expectedApiQuery = DOCKER_API_URL + "/repository/name" + "/tags?page_size=" + 10;
        when(restTemplate.getForObject(expectedApiQuery, String.class))
            .thenReturn("{ \"next\": \"http://next.url.to.fetch.from/\", \"results\": [\"response from docker hub\"]}");
        when(restTemplate.getForObject("http://next.url.to.fetch.from/", String.class))
            .thenReturn("{ \"next\": null, \"results\": [\"second response from docker hub\"]}");

        when(jsonObjectMappingService.mapToClass("[\"response from docker hub\"]", DockerHubImageInfo[].class, false))
            .thenReturn(new DockerHubImageInfo[]{new DockerHubImageInfo("id", new Date(123), "latest", "digest")});
        when(jsonObjectMappingService.mapToClass("[\"second response from docker hub\"]", DockerHubImageInfo[].class,
            false))
            .thenReturn(new DockerHubImageInfo[]{new DockerHubImageInfo("id2", new Date(345), "oldVersion",
                "otherDigest")});


        List<DockerHubImageInfo> imageInfos = daoUnderTest.getTags("repository/name", 10);

        assertThat(imageInfos).contains(
            new DockerHubImageInfo("id", new Date(123), "latest", "digest"),
            new DockerHubImageInfo("id2", new Date(345), "oldVersion", "otherDigest"));
    }
}
