package com.robertnator.docker.update.sensor.dao.dockerhub;

import com.robertnator.docker.update.sensor.model.dockerhub.DockerHubImageInfo;
import com.robertnator.docker.update.sensor.service.json.JsonObjectMappingException;
import com.robertnator.docker.update.sensor.service.json.JsonObjectMappingService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Component
public class DockerHubDao {

    private static final Logger logger = LoggerFactory.getLogger(DockerHubDao.class);

    public static String DOCKER_API_URL = "https://hub.docker.com/v2/repositories";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JsonObjectMappingService jsonObjectMappingService;

    public List<DockerHubImageInfo> getLatestTags(String imageName, String dockerHubNamespace, int numberOfTags)
        throws JsonObjectMappingException {
        List<DockerHubImageInfo> queryResults = new ArrayList<>();
        String queryURL = createTagQueryURL(imageName, dockerHubNamespace, numberOfTags);
        while (true) {
            JSONObject jsonResponse = executeGetQuery(queryURL);
            queryResults.addAll(asList(
                jsonObjectMappingService.mapToClass(jsonResponse.getJSONArray("results").toString(),
                    DockerHubImageInfo[].class, false)));
            if (!jsonResponse.isNull("next")) {
                queryURL = jsonResponse.getString("next");
            } else {
                break;
            }
        }
        logger.info("finished docker hub query with {} results found", queryResults.size());
        return queryResults;
    }

    private static String createTagQueryURL(String imageName, String dockerHubNamespace, int numberOfTags) {
        String dockerHubRepository = imageName;
        if (isNotEmpty(dockerHubNamespace)) {
            dockerHubRepository = dockerHubNamespace + "/" + imageName;
        }
        return DOCKER_API_URL + "/" + dockerHubRepository + "/tags?page_size=" + numberOfTags;
    }

    private JSONObject executeGetQuery(String queryURL) {
        logger.info("executing query to docker hub: {}", queryURL);
        String response = restTemplate.getForObject(queryURL, String.class);
        logger.debug("received from docker hub: {}", response);
        return new JSONObject(response);
    }
}
