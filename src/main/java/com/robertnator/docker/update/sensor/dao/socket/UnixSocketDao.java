package com.robertnator.docker.update.sensor.dao.socket;

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

    public String get(File socketFile, String query) throws UnixSocketException {
        try (UnixSocketHttpClient httpClient = unixSocketHttpClientFactory.createUnixSocketHttpClient(socketFile)) {
            URI uri = URI.create("http://localhost").resolve(query);
            HttpResponseWrapper response = executeGet(httpClient, socketFile, uri);
            if (response.getStatusCode() == 200) {
                String result = getResponseString(response);
                LOG.info("Finished reading response from socket: {}", result);
                return result;
            }
            throw new UnixSocketException(String.format("Unexpected status code '%s' on query '%s' to socket: " +
                "%s\nReason: %s", response.getStatusCode(), uri, socketFile, response.getReason()));
        } catch (IOException e) {
            throw new UnixSocketException("Unable to close http client.", e);
        }
    }


    private static HttpResponseWrapper executeGet(UnixSocketHttpClient httpClient, File socketFile, URI uri)
        throws UnixSocketException {
        try {
            LOG.info("Executing GET query '{}' on socket file '{}'", uri, socketFile);
            return httpClient.get(uri);
        } catch (IOException e) {
            throw new UnixSocketException(String.format("Error on GET query %s to: %s", uri, socketFile), e);
        }
    }

    private static String getResponseString(HttpResponseWrapper response) throws UnixSocketException {
        try {
            return response.getResponseAsString();
        } catch (IOException e) {
            throw new UnixSocketException("Unable to unpack response from query to unix socket.", e);
        }
    }
}
