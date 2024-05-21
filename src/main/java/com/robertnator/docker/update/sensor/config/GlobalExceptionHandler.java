package com.robertnator.docker.update.sensor.config;

import com.robertnator.docker.update.sensor.dao.socket.UnixSocketException;
import com.robertnator.docker.update.sensor.service.DockerImageUpdateCheckException;
import com.robertnator.docker.update.sensor.service.json.JsonObjectMappingException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

    @ExceptionHandler(DockerImageUpdateCheckException.class)
    public ResponseEntity<String> handleDockerImageUpdateCheckException(DockerImageUpdateCheckException e) {
        LOG.error("Error occurred while checking for update of docker image: ", e);
        return ResponseEntity.status(NOT_FOUND)
            .body(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleValidationExceptions(ConstraintViolationException e) {
        StringBuilder errors = new StringBuilder();
        e.getConstraintViolations().forEach((error) -> {
            String errorMessage = error.getMessage();
            errors.append(errorMessage).append(". ");
        });
        LOG.error("Received invalid request: ", e);
        return ResponseEntity.status(BAD_REQUEST)
            .body(errors.toString());
    }
}