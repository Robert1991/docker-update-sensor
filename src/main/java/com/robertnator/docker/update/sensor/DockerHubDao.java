package com.robertnator.docker.update.sensor;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.util.Arrays.asList;

@Component
public class DockerHubDao {
    private static final Logger logger = LoggerFactory.getLogger(DockerUpdateService.class);

    public static String DOCKER_API_URL = "https://hub.docker.com/v2/repositories";

    @Autowired
    private RestTemplateProvider restTemplateProvider;

    @Autowired
    private JsonObjectMappingService jsonObjectMappingService;

    public List<DockerHubImageInfo> getLatestTags(String imageName, int numberOfTags) throws JsonProcessingException {
        RestTemplate restTemplate = restTemplateProvider.create();
        String response = restTemplate.getForObject(DOCKER_API_URL + "/" + imageName + "/tags?page_size=" + numberOfTags,
                String.class);
        logger.info("received from docker hub: {}", response);
        JSONObject jsonResponse = new JSONObject(response);
        return asList(jsonObjectMappingService.mapToClass(jsonResponse.getJSONArray("results").toString(),
                DockerHubImageInfo[].class));
    }
}
