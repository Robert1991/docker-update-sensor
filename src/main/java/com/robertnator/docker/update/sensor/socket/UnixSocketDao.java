package com.robertnator.docker.update.sensor.socket;

import com.robertnator.docker.update.sensor.DockerUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URI;

@Component
public class UnixSocketDao {

    private static final Logger LOG = LoggerFactory.getLogger(DockerUpdateService.class);

    @Autowired
    private UnixSocketHttpClientFactory unixSocketHttpClientFactory;

    public String get(File socketFile, String query) throws IOException {
        try (UnixSocketHttpClient httpClient = unixSocketHttpClientFactory.createUnixSocketHttpClient(socketFile)) {
            URI uri = URI.create("http://localhost" + query);
            HttpResponseWrapper response = httpClient.get(uri);
            if (response.getStatusCode() == 200) {
                String result = response.getResponseAsString();
                LOG.info("Finished reading response from socket: {}", result);
                return result;
            }
            // throw exception in this case
            return "";
        }
    }
}
