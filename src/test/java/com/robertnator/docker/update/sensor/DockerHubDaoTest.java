package com.robertnator.docker.update.sensor;


import com.robertnator.docker.update.sensor.dao.dockerhub.DockerHubDao;
import com.robertnator.docker.update.sensor.dao.dockerhub.DockerHubImageInfo;
import com.robertnator.docker.update.sensor.dao.json.JsonObjectMappingException;
import com.robertnator.docker.update.sensor.dao.json.JsonObjectMappingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

import static com.robertnator.docker.update.sensor.dao.dockerhub.DockerHubDao.DOCKER_API_URL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DockerHubDaoTest {

    @Mock
    private JsonObjectMappingService jsonObjectMappingService;

    @Mock
    private RestTemplateProvider restTemplateProvider;

    @InjectMocks
    private DockerHubDao daoUnderTest;

    @Test
    void testGetLatestTags(@Mock RestTemplate restTemplate) throws JsonObjectMappingException {
        when(restTemplateProvider.create()).thenReturn(restTemplate);

        String expectedApiQuery = DOCKER_API_URL + "/repository/name" + "/tags?page_size=" + 10;
        when(restTemplate.getForObject(expectedApiQuery, String.class)).thenReturn(
            "{ \"results\": [\"response from docker hub\"] }");
        when(jsonObjectMappingService.mapToClass("[\"response from docker hub\"]", DockerHubImageInfo[].class))
            .thenReturn(new DockerHubImageInfo[]{new DockerHubImageInfo("id", new Date(123), "latest", "digest")});

        List<DockerHubImageInfo> imageInfos = daoUnderTest.getLatestTags("repository/name", 10);

        assertThat(imageInfos, contains(new DockerHubImageInfo("id", new Date(123), "latest", "digest")));
    }
}
