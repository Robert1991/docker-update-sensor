package com.robertnator.docker.update.sensor;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

import static com.robertnator.docker.update.sensor.DockerHubDao.DOCKER_API_URL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class DockerHubDaoTest {

    @MockBean
    private JsonObjectMappingService jsonObjectMappingService;

    @MockBean
    private RestTemplateProvider restTemplateProvider;

    @Autowired
    private DockerHubDao daoUnderTest;

    @Test
    void testGetLatestTags(@Mock RestTemplate restTemplate) throws JsonProcessingException {
        when(restTemplateProvider.create()).thenReturn(restTemplate);

        String expectedApiQuery = DOCKER_API_URL + "/repository/name" + "/tags?page_size=" + 10;
        when(restTemplate.getForObject(expectedApiQuery, String.class)).thenReturn("{ \"results\": [\"response from docker hub\"] }");
        when(jsonObjectMappingService.mapToClass("[\"response from docker hub\"]", DockerHubImageInfo[].class))
                .thenReturn(new DockerHubImageInfo[]{new DockerHubImageInfo("id", new Date(123), "latest", "digest")});

        List<DockerHubImageInfo> imageInfos = daoUnderTest.getLatestTags("repository/name", 10);

        assertThat(imageInfos, contains(new DockerHubImageInfo("id", new Date(123), "latest", "digest")));
    }
}
