package com.robertnator.docker.update.sensor.config;

import com.robertnator.docker.update.sensor.DockerUpdateService;
import com.robertnator.docker.update.sensor.dao.socket.UnixSocketException;
import com.robertnator.docker.update.sensor.service.json.JsonObjectMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DockerUpdateService.class);

    @ExceptionHandler(UnixSocketException.class)
    public ResponseEntity<String> handleUnixSocketException(UnixSocketException e) {
        LOG.error("Received error on unix socket communication:", e);
        return createInternalServerError(e.getMessage());
    }

    @ExceptionHandler(JsonObjectMappingException.class)
    public ResponseEntity<String> handleJsonObjectMappingException(JsonObjectMappingException e) {
        LOG.error("Received error when trying to map json response to class: ", e);
        return createInternalServerError(e.getMessage());
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<String> handleRestClientException(RestClientException e) {
        LOG.error("Received error on rest communication: ", e);
        return createInternalServerError(e.getMessage());
    }

    private static ResponseEntity<String> createInternalServerError(String message) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body(message);
    }
}