package com.robertnator.docker.update.sensor;

import com.robertnator.docker.update.sensor.socket.UnixSocketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DockerUpdateService.class);

    @ExceptionHandler(UnixSocketException.class)
    public ResponseEntity<String> handleUnixSocketException(UnixSocketException e) {
        LOG.error("Received error on unix socket communication:", e);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body(e.getMessage());
    }
}